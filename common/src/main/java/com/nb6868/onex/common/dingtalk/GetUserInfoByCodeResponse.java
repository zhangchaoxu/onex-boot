package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Schema(name = "通过code获取用户信息,返回体")
@EqualsAndHashCode(callSuper = false)
public class GetUserInfoByCodeResponse extends BaseResponse {

    @Schema(description = "用户信息")
    private UserInfo user_info;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserInfo implements Serializable {

        @Schema(description = "用户在钉钉上面的昵称")
        private String nick;

        @Schema(description = "用户在当前开放应用所属企业的唯一标识")
        private String unionid;

        @Schema(description = "用户在当前开放应用内的唯一标识")
        private String openid;

        @Schema(description = "用户主企业是否达到高级认证级别")
        private Boolean main_org_auth_high_level;
    }

}
