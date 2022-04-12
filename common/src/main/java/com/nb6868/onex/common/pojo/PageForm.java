package com.nb6868.onex.common.pojo;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.PageGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
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
        List<OrderItem> orderItems = new ArrayList<>();
        if (CollUtil.isNotEmpty(sortItems)) {
            sortItems.forEach(sortItem -> orderItems.add(sortItem.toOrderItem()));
        }
        return orderItems;
    }
}
