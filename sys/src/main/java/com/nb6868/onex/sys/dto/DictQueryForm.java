package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "字典查询")
public class DictQueryForm extends BasePageForm {

    @Query
    @ApiModelProperty("深度")
    private Integer deep;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,pcode")
    @ApiModelProperty("搜索关键词")
    private String search;

}
