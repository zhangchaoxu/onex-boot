package com.nb6868.onex.uc.dto;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.pojo.SortItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "角色查询")
public class RoleQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,name,remark")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @ApiModelProperty("编码")
    private String code;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

    @ApiModelProperty(value = "排序规则")
    @Query(exclude = "page", type = Query.Type.ORDER_BY)
    private List<SortItem> sortItems = CollUtil.newArrayList(new SortItem("sort"));

}
