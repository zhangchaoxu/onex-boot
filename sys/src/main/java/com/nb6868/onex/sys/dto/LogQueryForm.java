package com.nb6868.onex.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "日志记录查询")
public class LogQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @Query
    @ApiModelProperty("类型")
    private String type;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "uri,operation,content")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query(type = Query.Type.LIKE)
    @ApiModelProperty("创建用户")
    private String createName;

    @Query(type = Query.Type.BETWEEN_TIME, column = "create_time")
    @ApiModelProperty("创建时间区间")
    private List<String> createTimeRange;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

}
