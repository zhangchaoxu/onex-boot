package com.nb6868.onexboot.common.pojo;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.util.MessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 消息结果,简化的Result
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "消息结果")
public class MsgResult implements Serializable {

    @ApiModelProperty(value = "编码: 0表示成功,其他值表示失败")
    private int code = ErrorCode.SUCCESS;

    @ApiModelProperty(value = "消息内容")
    private String msg = "success";

    public boolean isSuccess(){
        return code == ErrorCode.SUCCESS;
    }

    public MsgResult success(String msg) {
        this.code = ErrorCode.SUCCESS;
        this.msg = msg;
        return this;
    }

    public MsgResult error() {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = MessageUtils.getMessage(this.code);
        return this;
    }

    public MsgResult error(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(this.code);
        return this;
    }

    public MsgResult error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public MsgResult error(String msg) {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
        return this;
    }



}
