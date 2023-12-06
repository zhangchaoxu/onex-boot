package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "字典查询")
public class DictQueryForm extends PageForm {

    @Query
    @Schema(description = "深度")
    private Integer deep;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,pcode")
    @Schema(description = "搜索关键词")
    private String search;

}
