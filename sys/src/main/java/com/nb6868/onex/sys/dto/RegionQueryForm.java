package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "区域查询")
public class RegionQueryForm extends PageForm {

    @Query
    @ApiModelProperty("深度")
    private Integer deep;

    @Query
    @ApiModelProperty("上级id")
    private Long pid;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,ext_name")
    @ApiModelProperty("搜索关键词")
    private String search;

}
