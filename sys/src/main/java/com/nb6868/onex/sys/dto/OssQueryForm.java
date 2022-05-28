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
@ApiModel(value = "OSS记录查询")
public class OssQueryForm extends BasePageForm {

    @Query(type = Query.Type.BETWEEN_TIME, column = "create_time")
    @ApiModelProperty("创建时间区间")
    private List<String> createTimeRange;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

}
