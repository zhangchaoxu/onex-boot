package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "站点查询")
public class SiteQueryForm extends BasePageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,descr,title")
    @ApiModelProperty("搜索关键词")
    private String search;

}
