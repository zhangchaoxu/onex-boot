package com.nb6868.onex.common.pojo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "基础排序请求")
public class SortItem implements Serializable {

    @ApiModelProperty(value = "排序字段")
    private String column;

    @ApiModelProperty(value = "升序true/倒序false")
    private Boolean asc = true;

    /**
     * 转换成OrderItem
     */
    public OrderItem toOrderItem() {
        return new OrderItem(StrUtil.toUnderlineCase(column), asc);
    }

}
