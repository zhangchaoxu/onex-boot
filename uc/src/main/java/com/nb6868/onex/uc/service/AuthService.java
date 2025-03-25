package com.nb6868.onex.uc.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.pojo.CaptchaReq;
import com.nb6868.onex.common.pojo.ChangeStateReq;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.CaptchaGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * 授权服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AuthService {

    @Autowired
    UserService userService;
    @Autowired
    BaseLogService logService;
    @Autowired
    BaseMsgService msgService;
    @Autowired
    CaptchaService captchaService;
    // 用户锁定时间
    TimedCache<String, String> userLockTimeCache = CacheUtil.newTimedCache(15 * DateUnit.MINUTE.getMillis());

    /**
     * 清空指定用户的登录锁定
     */
    public void removeUserLockTime(String userKey) {
        userLockTimeCache.remove(userKey);
    }

    /**
     * 获得所有用户锁定的key
     */
    public Set<String> getAllUserLockKey() {
        return userLockTimeCache.keySet();
    }

    /**
     * 校验验证码
     *
     * @param req          带有验证码的请求
     * @param magicCaptcha 魔术验证码
     */
    public void checkCaptcha(CaptchaReq req, String magicCaptcha) {
        // 先检验验证码表单
        ValidatorUtils.validateEntity(req, CaptchaGroup.class);
        // 再校验验证码与魔术验证码不同，并且 校验失败
        AssertUtils.isTrue(!StrUtil.equalsIgnoreCase(req.getCaptchaValue(), magicCaptcha) && !captchaService.validate(req.getCaptchaUuid(), req.getCaptchaValue()), ErrorCode.CAPTCHA_ERROR);
    }

    /**
     * 帐号密码登录
     *
     * @param tenantCode  租户编码
     * @param username    账号
     * @param password    密码(明文)
     * @param loginParams 登录参数
     * @return 登录用户
     */
    public UserEntity loginByUsernamePassword(String tenantCode, String username, String password, JSONObject loginParams) {
        // 从loginParams中获得登录配置参数
        String passwordErrorPolicy = loginParams.getStr("passwordErrorPolicy"); // 登录错误的处理策略rejectLogin/disableUser
        int passwordErrorLockMinute = loginParams.getInt("passwordErrorLockMinute", 15); // 登录错误锁定的时间分钟数
        int passwordErrorMinuteOffset = loginParams.getInt("passwordErrorMinuteOffset", 10); // 登录错误持续的时间长度
        int passwordErrorMaxTimes = loginParams.getInt("passwordErrorMaxTimes", 5); // 登录错误允许的次数
        if (StrUtil.equalsIgnoreCase(passwordErrorPolicy, "rejectLogin")) {
            // 错误策略是拒绝登录,查看是否有登录锁定时间
            DateTime userLockTime = DateUtil.parse(userLockTimeCache.get(username, false));
            AssertUtils.isTrue(DateUtil.compare(userLockTime, new DateTime()) > 0, ErrorCode.ACCOUNT_LOGIN_REJECT, "账号登录错误次数过多,请[" + DateUtil.format(userLockTime, DatePattern.NORM_DATETIME_PATTERN) + "]后再登录");
        }
        // 获得用户
        UserEntity user = userService.getByUsername(tenantCode, username);
        // 这里存在一个争论点，是否要将具体的信息告知用户
        // 告知的好处在于让用户有更清晰的错误提示ErrorCode.ACCOUNT_NOT_EXIST,但也会存在一个用户枚举遍历的风险
        if (user == null) {
            // 对于账号不存在的，也应该提供登录拒绝机制
            // 账号不存在的没有disableAccount的逻辑
            if (StrUtil.equalsIgnoreCase(passwordErrorPolicy, "rejectLogin")) {
                // 查询登录错误次数，查看是否达到error的次数
                int continuousLoginErrorTimes = logService.getContinuousLoginErrorTimes(username, tenantCode, passwordErrorMinuteOffset);
                if (continuousLoginErrorTimes >= passwordErrorMaxTimes - 1) {
                    // 锁定用户，写入缓存，并设定时间
                    userLockTimeCache.put(username, DateUtil.format(DateUtil.offsetMinute(new Date(), passwordErrorLockMinute), DatePattern.NORM_DATETIME_PATTERN), DateUnit.MINUTE.getMillis() * passwordErrorLockMinute);
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误超过{}次,请{}分钟后再登录,若忘记密码,请联系管理员", passwordErrorMinuteOffset, passwordErrorMaxTimes, passwordErrorLockMinute));
                } else {
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误{}次,超过{}次将被锁定登录,若忘记密码,请联系管理员", passwordErrorMinuteOffset, continuousLoginErrorTimes + 1, passwordErrorMaxTimes));
                }
            }
            throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        // 验证密码
        // 先对密码做解密,再做验证
        // String passwordPlaintext = PasswordUtils.aesDecode(form.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        boolean passwordVerify = PasswordUtils.verify(password, user.getPassword());
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);
        if (!passwordVerify) {
            // 账号存在,密码错误
            if (StrUtil.equalsIgnoreCase(passwordErrorPolicy, "rejectLogin")) {
                // 拒绝登录策略
                // 查询登录错误次数，查看是否达到error的次数
                int continuousLoginErrorTimes = logService.getContinuousLoginErrorTimes(username, tenantCode, passwordErrorMinuteOffset);
                if (continuousLoginErrorTimes >= passwordErrorMaxTimes - 1) {
                    // 锁定用户，写入缓存，并设定时间
                    userLockTimeCache.put(username, DateUtil.format(DateUtil.offsetMinute(new Date(), passwordErrorLockMinute), DatePattern.NORM_DATETIME_PATTERN), DateUnit.MINUTE.getMillis() * passwordErrorLockMinute);
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误超过{}次,请{}分钟后再登录,若忘记密码,请联系管理员", passwordErrorMinuteOffset, passwordErrorMaxTimes, passwordErrorLockMinute));
                } else {
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误{}次,超过{}次将被锁定登录,若忘记密码,请联系管理员", passwordErrorMinuteOffset, continuousLoginErrorTimes + 1, passwordErrorMaxTimes));
                }
            } else if (StrUtil.equalsIgnoreCase(passwordErrorPolicy, "disableUser")) {
                // 锁定用户策略
                // 若passwordErrorMinuteOffset分钟内,连续错误passwordErrorMaxTimes次,锁定账户
                int continuousLoginErrorTimes = logService.getContinuousLoginErrorTimes(username, tenantCode, passwordErrorMinuteOffset);
                if (continuousLoginErrorTimes >= passwordErrorMaxTimes - 1) {
                    // 锁定用户
                    ChangeStateReq changeStateForm = new ChangeStateReq();
                    changeStateForm.setState(UcConst.UserStateEnum.DISABLE.getCode());
                    changeStateForm.setId(user.getId());
                    userService.changeState(changeStateForm);
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误超过{}次,您的账户已被锁定,请联系管理员", passwordErrorMinuteOffset, passwordErrorMaxTimes));
                } else {
                    throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR, StrUtil.format("{}分钟内密码连续错误{}次,超过{}次将被锁定账户,若忘记密码,请联系管理员", passwordErrorMinuteOffset, continuousLoginErrorTimes + 1, passwordErrorMaxTimes));
                }
            } else {
                throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
            }
        } else {
            // 登录成功,清空缓存
            userLockTimeCache.remove(username);
        }
        return user;
    }

    /**
     * 手机号验证码登录
     *
     * @param tenantCode  租户编码
     * @param mobile      手机号
     * @param sms         短信验证码
     * @param loginParams 登录参数
     * @return 登录用户
     */
    public UserEntity loginByMobileSms(String tenantCode, String mobile, String sms, JSONObject loginParams) {
        // 获得用户
        UserEntity user = userService.getByMobile(tenantCode, mobile);
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);
        // 验证并将短信消费掉
        msgService.verifyMailCode(tenantCode, loginParams.getStr("mailTplCode", "CODE_LOGIN"), mobile, sms);
        return user;
    }
}
