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
@ApiModel(value = "根据unionid获取用户userid,返回体")
@EqualsAndHashCode(callSuper = false)
public class GetUserIdByUnionidResponse extends BaseResponse {

    @ApiModelProperty(value = "用户信息")
    private Result result;

    public GetUserIdByUnionidResponse(int errcode, String errmsg) {
        super(errcode, errmsg);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @ApiModelProperty(value = "联系类型,0：企业内部员工/1：企业外部联系人")
        private int contact_type;

        @ApiModelProperty(value = "用户userid")
        private String userid;
    }

}
