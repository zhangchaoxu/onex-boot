package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.Page;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础分页查询")
public class BasePageForm extends BaseForm {

    @ApiModelProperty("分页信息")
    @Page(groups = PageGroup.class)
    PageForm page;

}
