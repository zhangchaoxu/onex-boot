package com.nb6868.onex.common.dingtalk;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用结果
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResultResponse<T> extends BaseResponse {

    /**
     * 结果
     */
    private T result;

}