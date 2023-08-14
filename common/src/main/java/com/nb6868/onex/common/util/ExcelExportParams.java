package com.nb6868.onex.common.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Excel导出定义
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class ExcelExportParams {

    @ApiModelProperty("异步导出")
    private boolean async;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件夹名")
    private String folderName;

    @ApiModelProperty("列定义")
    private List<ColumnParams> columns;

    @Data
    public static class ColumnParams {

        @ApiModelProperty("标题")
        private String title;

        @ApiModelProperty("属性")
        private String property;

        @ApiModelProperty("反射方法")
        private String invokeMethod;

        @ApiModelProperty("是否链接")
        private boolean link;

        @ApiModelProperty("格式化方法,空用默认，time")
        private String fmt;

        @ApiModelProperty("时间格式化,fmt=time有效")
        private String timeFormat;

        @ApiModelProperty("文件夹名")
        private int width;

        @ApiModelProperty("枚举变量定义")
        private Map<String, Object> enmuMap;

        @ApiModelProperty("发生错误时候的默认值")
        private String errorDefaultMsg;
    }

}
