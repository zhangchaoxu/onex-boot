package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "部门查询")
public class DeptQueryForm extends PageForm {

    @Query
    @Schema(description = "类型")
    private Integer type;

    @Query
    @Schema(description = "编码")
    private String code;

    @Query
    @Schema(description = "上级编码")
    private String pcode;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,pcode")
    @Schema(description = "搜索关键词")
    private String search;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;
}
