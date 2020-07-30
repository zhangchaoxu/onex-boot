package com.nb6868.onex.common.resolver;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.util.HttpContextUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.annotation.LoginUser;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.service.TokenService;
import com.nb6868.onex.modules.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持的类型是:带有LoginUser注解,并且参数类型是UserEntity
        return parameter.getParameterType().isAssignableFrom(UserEntity.class) && parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) {
        LoginUser annotation = parameter.getParameterAnnotation(LoginUser.class);
        if (annotation == null) {
            return null;
        }

        // 验证token是否为空
        String token = HttpContextUtils.getRequestParameter(request, UcConst.TOKEN_HEADER);
        AssertUtils.isEmpty(token, ErrorCode.UNAUTHORIZED);

        // 验证token有效性
        Long userId = tokenService.getUserIdByToken(token);
        AssertUtils.isEmpty(userId, ErrorCode.UNAUTHORIZED);

        // 验证用户有效性
        UserEntity userEntity = userService.getById(userId);
        AssertUtils.isEmpty(userId, ErrorCode.UNAUTHORIZED);

        // request.setAttribute("user", userEntity, 1);
        if (annotation.renewalToken()) {
            // 是否token做延时
            tokenService.renewalToken(token, 60000L);
        }

        return userEntity;
    }
}
