package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "租户查询")
public class TenantQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,name,remark")
    @Schema(description = "关键词搜索")
    private String search;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query(type = Query.Type.BETWEEN_TIME, column = "create_time")
    @Schema(description = "创建时间区间")
    private List<String> createTimeRange;

}
