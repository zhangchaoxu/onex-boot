package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import com.nb6868.onex.common.pojo.SortItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "角色查询")
public class RoleQueryForm extends BasePageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "id,name,remark")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @ApiModelProperty("租户编码,对租户无效")
    private String tenantCode;

    @ApiModelProperty(value = "排序规则")
    @Query(exclude = "page", type = Query.Type.ORDER_BY)
    private List<SortItem> sortItems = Collections.singletonList(new SortItem("sort"));

}
