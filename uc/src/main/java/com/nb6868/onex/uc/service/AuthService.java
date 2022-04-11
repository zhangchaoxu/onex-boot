package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.auth.LoginForm;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.pojo.ChangeStateForm;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.entity.MailLogEntity;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 授权服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private BaseLogService logService;
    @Autowired
    private MailLogService mailLogService;

    /**
     * 帐号密码登录
     * @param form 登录请求
     * @param loginParams 登录参数
     * @return 登录用户
     */
    public UserEntity loginByUsernameAndPassword(LoginForm form, JSONObject loginParams) {
        ValidatorUtils.validateEntity(form, LoginForm.UsernamePasswordGroup.class);
        UserEntity user = userService.getByUsername(form.getTenantCode(), form.getUsername());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
        boolean passwordVerify = PasswordUtils.verify(form.getPassword(), user.getPassword());
        if (!passwordVerify) {
            // 密码错误
            if (loginParams.getBool("passwordErrorLock", false)) {
                // 若passwordErrorMinuteOffset分钟内,连续错误passwordErrorMaxTimes次,锁定账户
                int passwordErrorMinuteOffset = loginParams.getInt("passwordErrorMinuteOffset", 10);
                int passwordErrorMaxTimes = loginParams.getInt("passwordErrorMaxTimes", 5);
                int continuousLoginErrorTimes = logService.getContinuousLoginErrorTimes(form.getUsername(), form.getTenantCode(), passwordErrorMinuteOffset, passwordErrorMaxTimes - 1);
                if (continuousLoginErrorTimes >= passwordErrorMaxTimes - 1) {
                    // 锁定用户
                    ChangeStateForm changeStateForm = new ChangeStateForm();
                    changeStateForm.setState(UcConst.UserStateEnum.DISABLE.value());
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
     * @param form 登录请求
     * @param loginParams 登录参数
     * @return 登录用户
     */
    public UserEntity loginByMobileAndSms(LoginForm form, JSONObject loginParams) {
        // 校验参数
        ValidatorUtils.validateEntity(form, LoginForm.MobileSmsGroup.class);
        UserEntity user = userService.getByMobile(form.getTenantCode(), form.getMobile());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);

        // 获取最后一次短信记录
        MailLogEntity lastSmsLog = mailLogService.getLatestByTplCode(form.getTenantCode(), loginParams.getStr("mailTplCode", MsgConst.SMS_TPL_LOGIN), form.getMobile());
        AssertUtils.isTrue(lastSmsLog == null || !form.getSms().equalsIgnoreCase(lastSmsLog.getContentParams().getStr("code")), ErrorCode.SMS_CODE_ERROR);
        // 验证码正确,校验过期时间
        AssertUtils.isTrue(lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date()), ErrorCode.SMS_CODE_EXPIRED);
        // 将短信消费掉
        mailLogService.consumeById(lastSmsLog.getId());
        return user;
    }
}
