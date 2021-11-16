package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "企业内部应用的access_token,返回体")
@EqualsAndHashCode(callSuper = false)
public class AccessTokenResponse extends BaseResponse {

    @ApiModelProperty(value = "access_token的过期时间，单位秒")
    private int expires_in;

    @ApiModelProperty(value = "access_token")
    private String access_token;

    public AccessTokenResponse(int errcode, String errmsg) {
        super(errcode, errmsg);
    }

}
