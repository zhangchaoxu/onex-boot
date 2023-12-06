package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Schema(name = "根据userid获取用户详情")
@EqualsAndHashCode(callSuper = false)
public class GetUserDetailByUseridResponse extends BaseResponse {

     @Schema(description = "用户信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

         @Schema(description = "唯一id")
        private String unionid;

         @Schema(description = "用户userid")
        private String userid;

         @Schema(description = "名称")
        private String name;

         @Schema(description = "头像")
        private String avatar;

         @Schema(description = "国际电话区号")
        private String state_code;

         @Schema(description = "直属主管userId")
        private String manager_userid;

         @Schema(description = "手机号")
        private String mobile;

         @Schema(description = "工号")
        private String job_number;

         @Schema(description = "邮箱")
        private String email;

         @Schema(description = "职位")
        private String title;

         @Schema(description = "备注")
        private String remark;
    }

}
