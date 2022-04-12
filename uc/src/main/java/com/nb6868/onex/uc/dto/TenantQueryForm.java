package com.nb6868.onex.uc.dto;

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
@ApiModel(value = "租户查询")
public class TenantQueryForm extends BaseForm {

    @Query(blurryType = Query.BlurryType.OR, column = "code,name,remark")
    @ApiModelProperty("标题搜索")
    private String search;

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("分页信息")
    @Page(groups = PageGroup.class)
    PageForm page;

}
