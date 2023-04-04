package com.nb6868.onex.common.dingtalk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import java.io.Serializable;

@Setter
@Getter
@ApiModel(value = "部门id,返回体")
@EqualsAndHashCode(callSuper = false)
public class DeptIdListResponse extends BaseResponse {

    @ApiModelProperty(value = "信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @ApiModelProperty(value = "部门id列表")
        private List<String> dept_id_list;

    }

}
