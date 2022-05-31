package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "万年历查询")
public class CalenderQueryForm extends BasePageForm {

    @Query(type = Query.Type.IN)
    @ApiModelProperty("类型")
    private List<Integer> typeList;

    @Query(type = Query.Type.BETWEEN_TIME, column = "day_date")
    @ApiModelProperty("时间区间")
    private List<String> dayDateRange;

}
