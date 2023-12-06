package com.nb6868.onex.common.dingtalk;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "钉钉基础,返回体")
public class BaseResponse implements Serializable {

    @Schema(description = "请求ID")
    private String request_id;

    @Schema(description = "返回码")
    private int errcode = 0;

    @Schema(description = "返回描述")
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

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
