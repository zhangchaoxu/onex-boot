package com.nb6868.onex.common.pojo;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 表格数据
 * @author Charles zhangchoaxu@gmail.com
 */
@Data
public class TableData<T> extends PageData<T> {

    @ApiModelProperty(value = "表名称")
    private String title;

    @ApiModelProperty(value = "导出地址")
    private String exportUrl;

    @ApiModelProperty(value = "是否支持导出")
    private boolean export = false;

    @ApiModelProperty(value = "是否分页")
    private boolean page = true;

    @ApiModelProperty(value = "表头定义")
    private List<Dict> schemas;

    @ApiModelProperty(value = "额外数据")
    private Dict ext;

}
