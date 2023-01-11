package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.util.MessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * API 返回结果
 * 参考 {https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/api/R.java}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息码:0表示成功,其他值表示失败")
    private int code = ErrorCode.SUCCESS;

    @ApiModelProperty(value = "消息内容")
    private String msg = "success";

    @ApiModelProperty(value = "消息数据")
    private T data;

    @ApiModelProperty(value = "消息Unix时间戳")
    private Long time = Instant.now().toEpochMilli();

    public boolean isSuccess(){
        return code == ErrorCode.SUCCESS;
    }

    public Result<T> success() {
        return this;
    }

    public Result<T> success(T data) {
        this.setData(data);
        return this;
    }

    public Result<T> success(String msg, T data) {
        this.msg = msg;
        this.setData(data);
        return this;
    }

    public Result<T> error() {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = MessageUtils.getMessage(this.code);
        return this;
    }

    public Result<T> error(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(this.code);
        return this;
    }

    public Result<T> error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public Result<T> error(String msg) {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
        return this;
    }

    public Result<T> bool(boolean bool) {
        return bool ? success() : error();
    }

    public Result<T> bool(boolean bool, int code,  String msg) {
        return bool ? success() : error(code, msg);
    }

    public Result<T> bool(boolean bool, String msg) {
        return bool ? success() : error(msg);
    }

}
