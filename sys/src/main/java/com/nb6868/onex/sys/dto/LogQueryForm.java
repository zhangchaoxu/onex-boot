package com.nb6868.onex.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.validator.Page;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "日志记录查询")
public class LogQueryForm extends BaseForm {

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @Query
    @ApiModelProperty("类型")
    private String type;

    @Query(blurryType = Query.BlurryType.OR, column = "uri,operation,content")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query(type = Query.Type.LIKE)
    @ApiModelProperty("创建用户")
    private String createName;

    @Query(type = Query.Type.GE, column = "create_time")
    @ApiModelProperty("开始时间")
    private String startCreateTime;

    @Query(type = Query.Type.LE, column = "create_time")
    @ApiModelProperty("结束时间")
    private String endCreateTime;

    @Query
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tenantCode;

    @ApiModelProperty("分页信息")
    @Page(groups = PageGroup.class)
    PageForm page;

}
