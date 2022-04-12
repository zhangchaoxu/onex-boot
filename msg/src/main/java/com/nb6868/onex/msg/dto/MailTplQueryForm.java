package com.nb6868.onex.msg.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.PageForm;
import com.nb6868.onex.common.validator.Page;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息模板查询")
public class MailTplQueryForm extends BaseForm {

    @Query
    @ApiModelProperty("编码")
    private String code;

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @Query(blurryType = Query.BlurryType.OR, column="name,title,content")
    @ApiModelProperty("关键词搜索")
    private String search;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

    @ApiModelProperty("分页参数")
    @Page(groups = PageGroup.class)
    PageForm page;

}
