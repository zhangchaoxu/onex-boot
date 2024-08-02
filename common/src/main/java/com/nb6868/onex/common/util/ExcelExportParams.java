package com.nb6868.onex.common.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Excel导出定义
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Accessors(chain = true)
public class ExcelExportParams {

    @Schema(description = "异步导出")
    private boolean async;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件夹名")
    private String folderName;

    @Schema(description = "文件模板路径")
    private String templateFile;

    @Schema(description = "渲染方式")
    private String renderType;

    @Schema(description = "表头高度")
    private int headerHeight;

    @Schema(description = "列高度")
    private int rowHeight;

    @Schema(description = "其它额外的元数据")
    private Map<String, Object> metaInfo;

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

        @Schema(description = "宽度")
        private int width;

        @Schema(description = "水平居中方式")
        private Integer horizontalAlignment;

        @Schema(description = "垂直居中方式")
        private Integer verticalAlignment;

        @Schema(description = "文字自动换行")
        private Boolean wrapText;

        @Schema(description = "空数值填充")
        private String emptyToDefault;

        @Schema(description = "枚举变量定义")
        private Map<String, Object> enmuMap;

        @Schema(description = "发生错误时候的默认值")
        private String errorDefaultMsg;
    }

}
