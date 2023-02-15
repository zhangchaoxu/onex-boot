package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础分页查询")
public class BasePageForm extends BaseForm {

    @ApiModelProperty(value = "页码")
    @Min(value = 1, message = "页码不能小于1", groups = PageGroup.class)
    @NotNull(message = "页数不能为空", groups = PageGroup.class)
    private Long pageNo;

    @ApiModelProperty(value = "页数")
    @Min(value = 1, message = "页数不能小于1", groups = PageGroup.class)
    @NotNull(message = "页数不能为空", groups = PageGroup.class)
    @Query(exclude = "page", type = Query.Type.LIMIT)
    private Long pageSize;

    @ApiModelProperty(value = "排序规则")
    @Query(exclude = "page", type = Query.Type.ORDER_BY)
    private List<SortItem> sortItems;

    public PageForm getPage() {
        PageForm page = new PageForm();
        page.setPageNo(pageNo == null ? 1 : pageNo);
        page.setPageSize(pageSize == null ? 10 : pageSize);
        page.setSortItems(sortItems);
        return page;
    }

}
