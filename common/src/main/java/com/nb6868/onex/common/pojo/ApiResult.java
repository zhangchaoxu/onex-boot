package com.nb6868.onex.common.pojo;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * API 请求结果，将所有第三方等接口调用结果格式化
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> implements Serializable {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "消息码")
    private String code;

    @Schema(description = "消息内容")
    private String msg;

    @Schema(description = "消息数据")
    private T data;

    public static <T> ApiResult<T> of() {
       /* @SuppressWarnings("unchecked") final ApiResult<T> t = (ApiResult<T>) EMPTY;
        return t;*/
        return new ApiResult<>();
    }

    public static <T> ApiResult<T> of(final T value) {
        return new ApiResult<T>().setData(value);
    }

    public ApiResult<T> success() {
        this.setSuccess(true);
        return this;
    }

    public ApiResult<T> success(T data) {
        this.setSuccess(true);
        this.setData(data);
        return this;
    }

    public ApiResult<T> success(String msg, T data) {
        this.setSuccess(true);
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

    public ApiResult<T> error() {
        this.setSuccess(false);
        return this;
    }

    public ApiResult<T> error(String code, String msg) {
        this.setSuccess(false);
        this.setCode(code);
        this.setMsg(msg);
        return this;
    }

    public ApiResult<T> copy(ApiResult<?> apiResult) {
        this.setSuccess(apiResult.isSuccess());
        this.setCode(apiResult.getCode());
        this.setMsg(apiResult.getMsg());
        return this;
    }

    public String getCodeMsg() {
        return StrUtil.format("{}:{}", StrUtil.nullToEmpty(getCode()), StrUtil.nullToEmpty(getMsg()));
    }

}
