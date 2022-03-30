package com.nb6868.onex.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author Charles (zhancgchaoxu@gmail.com)
 */
@RestControllerAdvice
@ConditionalOnProperty(name = "onex.exception-handler.enable", havingValue = "true")
@Slf4j
public class OnexExceptionHandler extends BaseExceptionHandler {

}
