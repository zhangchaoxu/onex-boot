package com.nb6868.onex.sched.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.BasePageForm;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.validator.Page;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务查询")
public class TaskQueryForm extends BasePageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,remark")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

}
