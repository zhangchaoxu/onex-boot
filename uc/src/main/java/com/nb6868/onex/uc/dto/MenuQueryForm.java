package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "菜单查询")
public class MenuQueryForm extends BaseForm {

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @Query
    @ApiModelProperty("租户编码,对租户无效")
    private String tenantCode;

}
