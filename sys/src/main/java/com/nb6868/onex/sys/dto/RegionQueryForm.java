package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "区域查询")
public class RegionQueryForm extends PageForm {

    @Query
    @Schema(description = "深度")
    private Integer deep;

    @Query
    @Schema(description = "上级id")
    private Long pid;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,ext_name")
    @Schema(description = "搜索关键词")
    private String search;

}
