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
@ApiModel(value = "定时任务查询")
public class TaskQueryForm extends BaseForm {

    @ApiModelProperty(value = "关键词搜索")
    @Query(blurryType = Query.BlurryType.OR, column = "name,remark")
    private String search;

    @ApiModelProperty(value = "状态")
    @Query
    private Integer state;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

    @ApiModelProperty("分页信息")
    @Page(groups = PageGroup.class)
    PageForm page;

}
