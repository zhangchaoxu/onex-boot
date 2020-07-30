package com.nb6868.onex.modules.aep.sdk;

import com.nb6868.onex.booster.exception.ErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * AEP返回消息
 * @author charles zhangchaoxu@gmail.com
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class HttpResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码: 0表示成功,其他值表示失败")
    private int code = ErrorCode.SUCCESS;

    @ApiModelProperty(value = "消息内容")
    private String msg = "ok";

    @ApiModelProperty(value = "响应数据")
    private T result;

    public HttpResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess(){
        return code == ErrorCode.SUCCESS;
    }

}
