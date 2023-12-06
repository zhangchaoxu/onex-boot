package com.nb6868.onex.common.pojo;

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

     @Schema(description = "列类型,selection/index/expand")
    private String type;

     @Schema(description = "显示的标题")
    private String label;

     @Schema(description = "列内容字段名")
    private String prop;

     @Schema(description = "链接对应字段名")
    private String linkProp;

     @Schema(description = "列宽度")
    private String width;

     @Schema(description = "列最小宽度")
    private String minWidth;

     @Schema(description = "当内容过长被隐藏时显示 tooltip")
    private boolean showOverflowTooltip = true;

}
