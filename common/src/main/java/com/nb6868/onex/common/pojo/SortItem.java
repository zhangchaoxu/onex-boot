package com.nb6868.onex.common.pojo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(value = "基础排序请求")
public class SortItem implements Serializable {

    public SortItem(String column) {
        this.column = column;
    }

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
