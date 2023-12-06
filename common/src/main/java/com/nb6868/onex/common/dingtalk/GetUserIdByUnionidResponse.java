package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Schema(name = "根据unionid获取用户userid,返回体")
@EqualsAndHashCode(callSuper = false)
public class GetUserIdByUnionidResponse extends BaseResponse {

    @Schema(description = "用户信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @Schema(description = "联系类型,0：企业内部员工/1：企业外部联系人")
        private int contact_type;

        @Schema(description = "用户userid")
        private String userid;
    }

}
