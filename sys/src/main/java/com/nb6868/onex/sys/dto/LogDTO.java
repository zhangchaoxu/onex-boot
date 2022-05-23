package com.nb6868.onex.sys.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "日志")
public class LogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "请求URI")
    private String uri;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "用户操作")
    private String operation;

    @ApiModelProperty(value = "请求参数")
    private JSONObject requestParams;

    @ApiModelProperty(value = "耗时(毫秒)")
    private Long requestTime;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "用户名")
    private String createName;

    @ApiModelProperty(value = "租户编码")
    private String tenantCode;

}
