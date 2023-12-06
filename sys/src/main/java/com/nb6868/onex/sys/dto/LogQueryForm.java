package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "日志记录查询")
public class LogQueryForm extends PageForm {

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "类型")
    private String type;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "uri,operation,content")
    @Schema(description = "关键词搜索")
    private String search;

    @Query(type = Query.Type.LIKE)
    @Schema(description = "创建用户")
    private String createName;

    @Query(type = Query.Type.BETWEEN_TIME, column = "create_time")
    @Schema(description = "创建时间区间")
    private List<String> createTimeRange;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}
