package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "登录结果")
public class LoginRes extends BaseRes {

    @Schema(description = "token")
    private String token;

    @Schema(description = "token的header key")
    private String tokenKey;

    @Schema(description = "用户信息")
    private Object user;

}
