package com.nb6868.onex.sys.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "日志")
public class LogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

     @Schema(description = "类型")
    private String type;

     @Schema(description = "请求URI")
    private String uri;

     @Schema(description = "内容")
    private String content;

     @Schema(description = "用户操作")
    private String operation;

     @Schema(description = "请求参数")
    private JSONObject requestParams;

     @Schema(description = "耗时(毫秒)")
    private Long requestTime;

     @Schema(description = "状态")
    private Integer state;

     @Schema(description = "用户名")
    private String createName;

     @Schema(description = "租户编码")
    private String tenantCode;

}
