package com.nb6868.onex.api.common.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.nb6868.onex.api.modules.log.entity.ErrorEntity;
import com.nb6868.onex.api.modules.log.service.ErrorService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * 异常处理器
 *
 * @author Charles (zhancgchaoxu@gmail.com)
 */
@RestControllerAdvice
@Slf4j
public class OnexExceptionHandler {

    /**
     * 当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private ErrorService errorService;

    /**
     * 处理自定义异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(OnexException.class)
    public Object handleOnexException(HttpServletRequest request, OnexException e) {
        return handleExceptionResult(request, e.getCode(), e.getMsg());
    }

    /**
     * 处理主键重复异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Object handleDuplicateKeyException(HttpServletRequest request, DuplicateKeyException e) {
        return handleExceptionResult(request, ErrorCode.DB_RECORD_EXISTS);
    }

    /**
     * 处理方法不支持
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return handleExceptionResult(request, ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 处理参数错误
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST);
    }

    /**
     * 处理404
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        // 解决跨域问题
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader(HttpHeaders.ORIGIN));
        return handleExceptionResult(request, ErrorCode.NOT_FOUND);
    }

    /**
     * 处理Shiro未授权异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleNoHandlerFoundException(HttpServletRequest request, UnauthorizedException e) {
        return handleExceptionResult(request, ErrorCode.FORBIDDEN);
    }

    /**
     * 处理授权失败异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public Object handleUnauthenticatedExceptionException(HttpServletRequest request, UnauthenticatedException e) {
        return handleExceptionResult(request, ErrorCode.UNAUTHORIZED);
    }

    /**
     * 处理微信接口异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(WxErrorException.class)
    public Object handleWxErrorException(HttpServletRequest request, WxErrorException e) {
        log.error(e.getMessage(), e);
        // 保存日志
        saveLog(e);
        return handleExceptionResult(request, ErrorCode.WX_API_ERROR);
    }

    /**
     * 数据库结构异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        // 保存日志
        saveLog(e);
        return handleExceptionResult(request, ErrorCode.DB_VIOLATION_ERROR, "dev".equalsIgnoreCase(env) ? MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR) : null);
    }

    /**
     * 参数校验异常
     * 用于在方法中对于@RequestBody和@RequestParam例如@NotNull @NotEmpty的校验
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        Locale.setDefault(LocaleContextHolder.getLocale());
        // 参考ValidatorUtils
        // 需要在Controller中加上Validated注解,需要在接口方法参数中加上NotNull NotEmpty等校验注解
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        // 返回所有错误;分割
        StringJoiner errorMsg = new StringJoiner(";");
        constraintViolations.forEach(objectConstraintViolation -> errorMsg.add(objectConstraintViolation.getMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg.toString());
    }

    /**
     * 方法参数校验无效异常
     * 用于校验@RequestBody中实体的注解
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Locale.setDefault(LocaleContextHolder.getLocale());
        // 获取所有的错误
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        // 返回所有错误;分割
        StringJoiner errorMsg = new StringJoiner(";");
        errors.forEach(x -> errorMsg.add(x.getDefaultMessage()));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg.toString());
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(HttpServletRequest request, MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        // 保存日志
        saveLog(e);
        return handleExceptionResult(request, ErrorCode.FILE_EXCEED_MAX_FILE_SIZE, "dev".equalsIgnoreCase(env) ? MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR) : null);
    }

    /**
     * HttpMessageNotReadableException
     * RequestBody中的数据格式转换失败报错,比如定义的int传值是string
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        saveLog(e);
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, "参数错误");
    }

    /**
     * 处理其它异常
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        // 保存日志
        saveLog(e);
        return handleExceptionResult(request, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理exception返回结果
     * @param request 请求
     * @param code 错误码
     * @param msg 错误消息
     * @return
     */
    private Object handleExceptionResult(HttpServletRequest request, int code, String msg) {
        if (ObjectUtils.isEmpty(msg)) {
            msg = MessageUtils.getMessage(code);
        }
        if (request != null && request.getRequestURI().contains("/html/")) {
            // html页面,返回错误页面
            ModelAndView map= new ModelAndView("msg");
            map.addObject("type", "warn");
            map.addObject("title", code);
            map.addObject("message", msg);
            return map;
        } else {
            // rest请求返回result
            return new Result<>().error(code, msg);
        }
    }

    private Object handleExceptionResult(HttpServletRequest request, int code) {
        return handleExceptionResult(request, code, MessageUtils.getMessage(code));
    }

    /**
     * 保存异常日志
     */
    private void saveLog(Exception ex) {
        ErrorEntity log = new ErrorEntity();
        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            log.setIp(HttpContextUtils.getIpAddr(request));
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setUri(request.getRequestURI());
            log.setMethod(request.getMethod());
            Map<String, String> params = HttpContextUtils.getParameterMap(request);
            if (!CollectionUtils.isEmpty(params)) {
                log.setParams(JacksonUtils.pojoToJson(params));
            }
        }
        // 异常信息
        log.setContent(ExceptionUtil.stacktraceToString(ex));
        // 保存
        try {
            errorService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
