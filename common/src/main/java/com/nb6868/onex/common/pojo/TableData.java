package com.nb6868.onex.common.pojo;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 表格数据
 * @author Charles zhangchoaxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableData<T> extends PageData<T> {

     @Schema(description = "表名称")
    private String title;

     @Schema(description = "导出地址")
    private String exportUrl;

     @Schema(description = "是否支持导出")
    private boolean export = false;

     @Schema(description = "是否分页")
    private boolean page = true;

     @Schema(description = "表头定义")
    private List<Dict> schemas;

     @Schema(description = "额外数据")
    private Dict ext;

}
