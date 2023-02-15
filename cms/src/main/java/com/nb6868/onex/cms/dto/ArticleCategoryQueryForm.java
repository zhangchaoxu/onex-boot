package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "文章分类查询")
public class ArticleCategoryQueryForm extends PageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,descr,title")
    @ApiModelProperty("搜索关键词")
    private String search;

}
