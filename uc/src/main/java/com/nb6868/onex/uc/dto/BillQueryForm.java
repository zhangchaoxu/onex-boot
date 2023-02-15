package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "参数查询")
public class BillQueryForm extends PageForm {

    @Query
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @Query(type = Query.Type.LIKE)
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Query()
    @ApiModelProperty(value = "类型")
    private String type;

    @Query
    @ApiModelProperty(value = "账单类型")
    private String billType;

    @Query
    @ApiModelProperty(value = "状态")
    private Integer state;

    @Query
    @ApiModelProperty(value = "关联id")
    private Long relationId;

}
