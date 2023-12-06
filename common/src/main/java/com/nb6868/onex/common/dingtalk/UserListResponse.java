package com.nb6868.onex.common.dingtalk;

import cn.hutool.json.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Schema(name = "用户信息列表,返回体")
@EqualsAndHashCode(callSuper = false)
public class UserListResponse extends BaseResponse {

     @Schema(description = "信息")
    private Result result;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Result implements Serializable {

         @Schema(description = "下一个游标")
        private Integer next_cursor;

         @Schema(description = "是否还有下一页")
        private Boolean has_more;

         @Schema(description = "实际数据数组")
        private JSONArray list;

    }

}
