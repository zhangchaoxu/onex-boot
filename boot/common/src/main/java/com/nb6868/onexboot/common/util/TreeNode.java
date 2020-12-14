package com.nb6868.onexboot.common.util;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点，所有需要实现树节点的，都需要继承该类
 * 通过id和pid来控制继承关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TreeNode<T> extends BaseDTO {

    /**
     * 上级ID
     */
    @ApiModelProperty(value = "上级ID")
    @NotNull(message="请选择上级", groups = DefaultGroup.class)
    private Long pid;

    /**
     * 子节点列表
     */
    @ApiModelProperty(value = "子节点列表")
    private List<T> children = new ArrayList<>();

}
