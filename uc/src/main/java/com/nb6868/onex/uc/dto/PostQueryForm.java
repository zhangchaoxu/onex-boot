package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "岗位查询")
public class PostQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty("编码")
    private String code;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,remark")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @ApiModelProperty("租户编码,对租户无效")
    private String tenantCode;

}
