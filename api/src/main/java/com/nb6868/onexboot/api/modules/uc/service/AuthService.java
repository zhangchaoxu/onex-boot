package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.common.config.LoginPropsSource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 登录授权相关服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AuthService {

    @Autowired
    private OnexProps onexProps;
    @Autowired
    private ParamService paramService;

    /**
     * 通过登录类型获取登录配置
     *
     * @param loginType 登录类型
     * @return 登录配置
     */
    public OnexProps.LoginProps getLoginProps(String loginType) {
        OnexProps.LoginProps loginProps;
        if (onexProps.getLoginPropsSource() == LoginPropsSource.PROPS) {
            loginProps = onexProps.getLoginProps();
        } else {
            loginProps = paramService.getCombineContentObject(loginType, OnexProps.LoginProps.class);
        }
        return loginProps;
    }

}
