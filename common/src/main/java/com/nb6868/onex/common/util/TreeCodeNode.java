package com.nb6868.onex.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点，所有需要实现树节点的，都需要继承该类
 * 通过code和pcode来控制继承关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TreeCodeNode<T> implements Serializable {

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "上级编码")
    private Long pcode;

    @ApiModelProperty(value = "子节点列表")
    private List<T> children = new ArrayList<>();

}
