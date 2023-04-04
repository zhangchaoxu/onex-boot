package com.nb6868.onex.common.dingtalk;

import cn.hutool.json.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel(value = "用户信息列表,返回体")
@EqualsAndHashCode(callSuper = false)
public class UserListResponse extends BaseResponse {

    @ApiModelProperty(value = "信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

        @ApiModelProperty(value = "下一个游标")
        private Integer next_cursor;

        @ApiModelProperty(value = "是否还有下一页")
        private Boolean has_more;

        @ApiModelProperty(value = "实际数据数组")
        private JSONArray list;

    }

}
