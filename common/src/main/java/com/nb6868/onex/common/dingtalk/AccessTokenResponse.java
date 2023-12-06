package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "企业内部应用的access_token,返回体")
@EqualsAndHashCode(callSuper = false)
public class AccessTokenResponse extends BaseResponse {

    @Schema(description = "access_token的过期时间，单位秒")
    private int expires_in;

    @Schema(description = "access_token")
    private String access_token;

}
