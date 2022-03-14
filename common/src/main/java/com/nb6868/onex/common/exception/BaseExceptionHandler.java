package com.nb6868.onex.common.exception;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 基础的ExceptionHandler.
 * 继承后加上@RestControllerAdvice
 *
 * @author Charles (zhancgchaoxu@gmail.com)
 */
@Slf4j
public abstract class BaseExceptionHandler {

    /**
     * 当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String env;

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
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
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
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
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
        saveLog(request, e);
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
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.DB_VIOLATION_ERROR, StrUtil.contains(env, "dev") ? MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR) : null);
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
        String errorMsg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg);
    }

    /**
     * 方法参数校验无效异常
     * 用于校验@Validated @RequestBody中实体的注解
     * 注意: @Validated失败，LogOperation不会处理
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        saveLog(request, e);
        Locale.setDefault(LocaleContextHolder.getLocale());
        String errorMsg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, errorMsg);
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(HttpServletRequest request, MaxUploadSizeExceededException e) {
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.FILE_EXCEED_MAX_FILE_SIZE, "dev".equalsIgnoreCase(env) ? MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR) : null);
    }

    /**
     * HttpMessageNotReadableException
     * RequestBody中的内容不符合json定义
     * RequestBody中的数据格式转换失败报错,比如定义的int传值是string
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
    }

    /**
     * MethodArgumentTypeMismatchException
     * RequestParam中内容不符合定义，比如定义的int传值是string
     *
     * @param e exception
     * @return result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.ERROR_REQUEST, e.getMessage());
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
        saveLog(request, e);
        return handleExceptionResult(request, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理exception返回结果
     *
     * @param request 请求
     * @param code    错误码
     * @param msg     错误消息
     * @return
     */
    protected Object handleExceptionResult(HttpServletRequest request, int code, String msg) {
        if (ObjectUtils.isEmpty(msg)) {
            msg = MessageUtils.getMessage(code);
        }
        if (request != null && request.getRequestURI().contains("/html/")) {
            // html页面,返回错误页面
            ModelAndView map = new ModelAndView("msg");
            map.addObject("type", "warn");
            map.addObject("title", code);
            map.addObject("message", msg);
            return map;
        } else {
            // rest请求返回result
            return new Result<>().error(code, msg);
        }
    }

    protected Object handleExceptionResult(HttpServletRequest request, int code) {
        return handleExceptionResult(request, code, MessageUtils.getMessage(code));
    }

   /*
    记得先 implements RequestBodyAdvice

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //判断是否有此注解,只有为true时才会执行afterBodyRead
        return methodParameter.getParameterAnnotation(RequestBody.class) != null;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return new MappingJacksonInputMessage(inputMessage.getBody(), inputMessage.getHeaders());
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }*/

    /**
     * 保存异常日志
     */
    protected abstract void saveLog(HttpServletRequest request, Exception ex);
    /*{
        LogEntity logEntity = new LogEntity();
        logEntity.setType("error");
        logEntity.setState(0);
        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            logEntity.setIp(HttpContextUtils.getIpAddr(request));
            logEntity.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            logEntity.setUri(request.getRequestURI());
            logEntity.setMethod(request.getMethod());
            Map<String, String> params = HttpContextUtils.getParameterMap(request);
            if (!CollectionUtils.isEmpty(params)) {
                logEntity.setParams(JacksonUtils.pojoToJson(params));
            }
        }
        // 异常信息
        logEntity.setContent(ExceptionUtil.stacktraceToString(ex));
        // 保存
        try {
            logService.save(logEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
