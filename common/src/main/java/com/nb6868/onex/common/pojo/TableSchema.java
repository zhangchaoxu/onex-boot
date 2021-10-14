package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * 表格头定义
 * see https://element.eleme.cn/#/zh-CN/component/table
 *
 * @author Charles zhangchoaxu@gmail.com
 */
@Data
public class TableSchema {

    @ApiModelProperty(value = "列类型,selection/index/expand")
    private String type;

    @ApiModelProperty(value = "显示的标题")
    private String label;

    @ApiModelProperty(value = "列内容字段名")
    private String prop;

    @ApiModelProperty(value = "链接对应字段名")
    private String linkProp;

    @ApiModelProperty(value = "列宽度")
    private String width;

    @ApiModelProperty(value = "列最小宽度")
    private String minWidth;

    @ApiModelProperty(value = "当内容过长被隐藏时显示 tooltip")
    private boolean showOverflowTooltip = true;

}
