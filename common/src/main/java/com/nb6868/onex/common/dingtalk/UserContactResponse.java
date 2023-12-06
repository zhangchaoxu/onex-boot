package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserContactResponse implements Serializable {

    @Schema(description = "nick")
    private String nick;

    @Schema(description = "avatarUrl")
    private String avatarUrl;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "openId")
    private String openId;

    @Schema(description = "unionId")
    private String unionId;

    @Schema(description = "email")
    private String email;

    @Schema(description = "stateCode")
    private String stateCode;

}
