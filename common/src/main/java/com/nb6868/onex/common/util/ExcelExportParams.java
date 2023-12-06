package com.nb6868.onex.common.util;

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

    @Schema(description = "异步导出")
    private boolean async;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件夹名")
    private String folderName;

    @Schema(description = "列定义")
    private List<ColumnParams> columns;

    @Data
    public static class ColumnParams {

        @Schema(description = "标题")
        private String title;

        @Schema(description = "属性")
        private String property;

        @Schema(description = "反射方法")
        private String invokeMethod;

        @Schema(description = "是否链接")
        private boolean link;

        @Schema(description = "格式化方法,空用默认，time")
        private String fmt;

        @Schema(description = "时间格式化,fmt=time有效")
        private String timeFormat;

        @Schema(description = "文件夹名")
        private int width;

        @Schema(description = "枚举变量定义")
        private Map<String, Object> enmuMap;

        @Schema(description = "发生错误时候的默认值")
        private String errorDefaultMsg;
    }

}
