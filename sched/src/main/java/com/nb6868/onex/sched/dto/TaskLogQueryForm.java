package com.nb6868.onex.sched.dto;

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
@ApiModel(value = "定时任务记录查询")
public class TaskLogQueryForm extends BaseForm {

    @ApiModelProperty(value = "任务ID")
    @Query
    private Long taskId;

    @ApiModelProperty(value = "状态")
    @Query
    private Integer state;

    @ApiModelProperty(value = "关键词搜索")
    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "task_name,result,error")
    private String search;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

    @ApiModelProperty("分页信息")
    @Page(groups = PageGroup.class)
    PageForm page;

}
