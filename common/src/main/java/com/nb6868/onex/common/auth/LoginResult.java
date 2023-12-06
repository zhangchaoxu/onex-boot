package com.nb6868.onex.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "登录结果")
public class LoginResult implements Serializable {

    @Schema(description = "token")
    private String token;

    @Schema(description = "token的header key")
    private String tokenKey;

    @Schema(description = "用户信息")
    private Object user;

}
