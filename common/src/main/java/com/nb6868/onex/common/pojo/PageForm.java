package com.nb6868.onex.common.pojo;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础分页请求")
public class PageForm extends BaseForm {

    @ApiModelProperty(value = "页数")
    @Min(value = 1, message = "页数不能小于1")
    private Long pageSize = 10L;

    @ApiModelProperty(value = "页码")
    @Min(value = 1, message = "页码不能小于1")
    private Long pageNo = 1L;

    @ApiModelProperty(value = "排序规则")
    private List<SortItem> sortItems;

    /**
     * 获得排序规则
     */
    public List<OrderItem> toOrderItems() {
        return CollStreamUtil.toList(sortItems, SortItem::toOrderItem);
    }
}
