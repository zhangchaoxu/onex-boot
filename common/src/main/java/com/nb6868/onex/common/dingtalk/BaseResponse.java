package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "钉钉基础,返回体")
public class BaseResponse implements Serializable {

    @ApiModelProperty(value = "请求ID")
    private String request_id;

    @ApiModelProperty(value = "返回码")
    private int errcode = 0;

    @ApiModelProperty(value = "返回描述")
    private String errmsg = "ok";

    /**
     * 是否执行成功
     */
    public boolean isSuccess() {
        return errcode == 0;
    }

    public BaseResponse error(int errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        return this;
    }
}
