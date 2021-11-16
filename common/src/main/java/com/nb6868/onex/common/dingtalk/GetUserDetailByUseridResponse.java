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
@ApiModel(value = "根据userid获取用户详情")
@EqualsAndHashCode(callSuper = false)
public class GetUserDetailByUseridResponse extends BaseResponse {

    @ApiModelProperty(value = "用户信息")
    private Result result;

    public GetUserDetailByUseridResponse(int errcode, String errmsg) {
        super(errcode, errmsg);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @ApiModelProperty(value = "唯一id")
        private String unionid;

        @ApiModelProperty(value = "用户userid")
        private String userid;

        @ApiModelProperty(value = "名称")
        private String name;

        @ApiModelProperty(value = "头像")
        private String avatar;

        @ApiModelProperty(value = "国际电话区号")
        private String state_code;

        @ApiModelProperty(value = "直属主管userId")
        private String manager_userid;

        @ApiModelProperty(value = "手机号")
        private String mobile;

        @ApiModelProperty(value = "工号")
        private String job_number;

        @ApiModelProperty(value = "邮箱")
        private String email;

        @ApiModelProperty(value = "职位")
        private String title;

        @ApiModelProperty(value = "备注")
        private String remark;
    }

}
