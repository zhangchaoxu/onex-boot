package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "部门查询")
public class DeptQueryForm extends PageForm {

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @Query
    @ApiModelProperty("编码")
    private String code;

    @Query
    @ApiModelProperty("上级编码")
    private String pcode;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,pcode")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;
}
