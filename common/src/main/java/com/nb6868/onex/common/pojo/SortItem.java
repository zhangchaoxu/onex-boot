package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "基础排序请求")
public class SortItem implements Serializable {

    public SortItem(String column) {
        this.column = column;
    }

    @ApiModelProperty(value = "排序字段")
    private String column;

    @ApiModelProperty(value = "升序true/倒序false")
    private Boolean asc = true;

}
