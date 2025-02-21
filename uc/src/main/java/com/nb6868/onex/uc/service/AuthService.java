package com.nb6868.onex.uc.service;

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
        // 获得用户
        UserEntity user = userService.getByUsername(tenantCode, username);
        // 这里存在一个争论点，是否要将具体的信息告知用户
        // 告知的好处在于让用户有更清晰的错误提示ErrorCode.ACCOUNT_NOT_EXIST,但也会存在一个用户枚举遍历的风险
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 验证密码
        // 先对密码做解密,再做严重
        // String passwordPlaintext = PasswordUtils.aesDecode(form.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        boolean passwordVerify = PasswordUtils.verify(password, user.getPassword());
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);
        if (!passwordVerify) {
            // 密码错误
            if (loginParams.getBool("passwordErrorLock", false)) {
                // 若passwordErrorMinuteOffset分钟内,连续错误passwordErrorMaxTimes次,锁定账户
                int passwordErrorMinuteOffset = loginParams.getInt("passwordErrorMinuteOffset", 10);
                int passwordErrorMaxTimes = loginParams.getInt("passwordErrorMaxTimes", 5);
                int continuousLoginErrorTimes = logService.getContinuousLoginErrorTimes(username, tenantCode, passwordErrorMinuteOffset, passwordErrorMaxTimes - 1);
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
