package com.nb6868.onex.uc.dto;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.pojo.SortItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "角色查询")
public class RoleQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,name,remark")
    @Schema(description = "搜索关键词")
    private String search;

    @Query
    @Schema(description = "编码")
    private String code;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

     @Schema(description = "排序规则")
    @Query(exclude = "page", type = Query.Type.ORDER_BY)
    private List<SortItem> sortItems = CollUtil.newArrayList(new SortItem("sort"));

}
