package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel(value = "通过code获取用户信息,返回体")
@EqualsAndHashCode(callSuper = false)
public class GetUserInfoByCodeResponse extends BaseResponse {

    @ApiModelProperty(value = "用户信息")
    private UserInfo user_info;

    public GetUserInfoByCodeResponse(int errcode, String errmsg) {
        super(errcode, errmsg);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserInfo implements Serializable {

        @ApiModelProperty(value = "用户在钉钉上面的昵称")
        private String nick;

        @ApiModelProperty(value = "用户在当前开放应用所属企业的唯一标识")
        private String unionid;

        @ApiModelProperty(value = "用户在当前开放应用内的唯一标识")
        private String openid;

        @ApiModelProperty(value = "用户主企业是否达到高级认证级别")
        private Boolean main_org_auth_high_level;
    }

}
