package com.nb6868.onex.common.dingtalk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import java.io.Serializable;

@Setter
@Getter
@Schema(name = "部门id,返回体")
@EqualsAndHashCode(callSuper = false)
public class DeptIdListResponse extends BaseResponse {

    @Schema(description = "信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @Schema(description = "部门id列表")
        private List<String> dept_id_list;

    }

}
