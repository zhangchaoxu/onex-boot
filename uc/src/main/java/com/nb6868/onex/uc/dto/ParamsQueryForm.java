package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "参数查询")
public class ParamsQueryForm extends PageForm {

    @Query
     @Schema(description = "类型")
    private Integer type;

    @Query(type = Query.Type.LIKE_RIGHT, column = "code")
     @Schema(description = "参数编码")
    private String codeRight;

    @Query(type = Query.Type.LIKE_LEFT, column = "code")
     @Schema(description = "参数编码")
    private String codeLeft;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,remark,content")
     @Schema(description = "关键词搜索")
    private String search;

    @Query
     @Schema(description = "参数编码")
    private String code;

    @Query
     @Schema(description = "租户编码")
    private String tenantCode;

     @Schema(description = "URL地址")
    @Query(column = "content->'$.url'")
    private String contentUrl;

}
