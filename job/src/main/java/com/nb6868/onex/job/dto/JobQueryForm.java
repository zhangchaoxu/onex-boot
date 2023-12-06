package com.nb6868.onex.job.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务查询")
public class JobQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,remark")
    @Schema(description = "关键词搜索")
    private String search;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}
