package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "参数查询")
public class BillQueryForm extends PageForm {

    @Query
    @Schema(description = "用户ID")
    private Long userId;

    @Query(type = Query.Type.LIKE)
    @Schema(description = "用户名")
    private String userName;

    @Query()
    @Schema(description = "类型")
    private String type;

    @Query
    @Schema(description = "账单类型")
    private String billType;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "关联id")
    private Long relationId;

}
