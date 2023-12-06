package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "分页请求")
public class PageForm extends BaseForm {

    @Schema(description = "页码")
    @Min(value = 1, message = "页码不能小于1", groups = PageGroup.class)
    @NotNull(message = "页数不能为空", groups = PageGroup.class)
    private Long pageNo;

    @Schema(description = "页数")
    @Min(value = 1, message = "页数不能小于1", groups = PageGroup.class)
    @NotNull(message = "页数不能为空", groups = PageGroup.class)
    @Query(exclude = "page", type = Query.Type.LIMIT)
    private Long pageSize;

    @Schema(description = "排序规则")
    @Query(exclude = "page", type = Query.Type.ORDER_BY)
    private List<SortItem> sortItems;

}
