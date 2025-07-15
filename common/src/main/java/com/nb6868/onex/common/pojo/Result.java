package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.util.MessageUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.Instant;

/**
 * API 返回结果
 * 参考 {<a href="https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/api/R.java">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息码:0表示成功,其他值表示失败")
    private int code = ErrorCode.SUCCESS;

    @Schema(description = "消息内容")
    private String msg = "success";

    @Schema(description = "消息数据")
    private T data;

    @Schema(description = "消息Unix时间戳")
    private Long time = Instant.now().toEpochMilli();

    @Schema(description = "链路id")
    private String traceId;

    public boolean isSuccess() {
        return code == ErrorCode.SUCCESS;
    }

    public Result<T> success() {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        return this;
    }

    public Result<T> success(T data) {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setData(data);
        return this;
    }

    public Result<T> success(String msg, T data) {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setMsg(msg);
        this.setData(data);
        return this;
    }

    public Result<T> error() {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setCode(ErrorCode.INTERNAL_SERVER_ERROR);
        this.setMsg(MessageUtils.getMessage(this.code));
        return this;
    }

    public Result<T> error(int code) {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setCode(code);
        this.setMsg(MessageUtils.getMessage(this.code));
        return this;
    }

    public Result<T> error(int code, String msg) {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setCode(code);
        this.setMsg(msg);
        return this;
    }

    public Result<T> error(String msg) {
        this.setTraceId(MDC.get(Const.TRACE_ID));
        this.setCode(ErrorCode.INTERNAL_SERVER_ERROR);
        this.setMsg(msg);
        return this;
    }

    public Result<T> bool(boolean bool) {
        return bool ? success() : error();
    }

    public Result<T> bool(boolean bool, int code, String msg) {
        return bool ? success() : error(code, msg);
    }

    public Result<T> bool(boolean bool, String msg) {
        return bool ? success() : error(msg);
    }

}
