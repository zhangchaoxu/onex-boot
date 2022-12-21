package com.nb6868.onex.sched.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务记录查询")
public class SchedLogQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty("任务ID")
    private Long taskId;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "task_name,result,error")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

}
