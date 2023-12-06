package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "万年历查询")
public class CalenderQueryForm extends PageForm {

    @Query(type = Query.Type.IN)
    @Schema(description = "类型")
    private List<Integer> typeList;

    @Query(type = Query.Type.BETWEEN_TIME, column = "day_date")
    @Schema(description = "时间区间")
    private List<String> dayDateRange;

}
