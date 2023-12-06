package com.nb6868.onex.msg.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "消息模板查询")
public class MsgTplQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,title,content")
    @Schema(description = "关键词搜索")
    private String search;

    @Query
    @Schema(description = "编码")
    private String code;

    @Query
    @Schema(description = "类型")
    private Integer type;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}
