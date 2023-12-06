package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "OSS记录查询")
public class OssQueryForm extends PageForm {

    @Query(type = Query.Type.BETWEEN_TIME, column = "create_time")
    @Schema(description = "创建时间区间")
    private List<String> createTimeRange;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}
