package com.nb6868.onex.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.oss.OssLocalUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Hutool实现的Excel导出工具
 * see {https://hutool.cn/docs/index.html#/poi/Excel%E7%94%9F%E6%88%90-ExcelWriter}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ExcelExportUtils {

    public final static String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    public final static String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    public final static String FILENAME_XLS_FMT = "attachment;filename={}.xls";
    public final static String FILENAME_XLSX_FMT = "attachment;filename={}.xlsx";
    public final static String FILENAME_XLS_SUFFIX = ".xls";
    public final static String FILENAME_XLSX_SUFFIX = ".xlsx";

    /**
     * 通过workbook 下载excel
     */
    public static void exportXlsxToServlet(HttpServletResponse response, String fileName, ExcelWriter writer) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(CONTENT_TYPE_XLSX);
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format(FILENAME_XLSX_FMT, URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())));
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            // 关闭writer，释放内存
            writer.close();
            IoUtil.close(out);
        } catch (IOException e) {
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
    }

    /**
     * 格式化列显示内容
     *
     * @param bean   实体bean
     * @param column 列定义
     */
    public static String formatColumnValue(Object bean, ExcelExportParams.ColumnParams column, Function<Dict, String> function) {
        String fmt = column.getFmt();
        if (StrUtil.isBlank(fmt)) {
            return BeanUtil.getProperty(bean, column.getProperty());
        } else {
            String pValue = "";
            if ("time".equalsIgnoreCase(fmt)) {
                // 时间格式化
                Object pObject = BeanUtil.getProperty(bean, column.getProperty());
                if (pObject instanceof Long) {
                    long timestamp = (long) pObject;
                    pValue = DateUtil.format(DateUtil.date((timestamp < 1000000000000L ? 1000 : 1) * timestamp), column.getTimeFormat());
                } else if (pObject instanceof Date) {
                    pValue = DateUtil.format((Date) pObject, column.getTimeFormat());
                } else if (pObject instanceof LocalDateTime) {
                    pValue = DateUtil.format((LocalDateTime) pObject, column.getTimeFormat());
                }
            } else if (function != null) {
                pValue = function.apply(Dict.create().set("bean", bean).set("column", column));
            }
            return pValue;
        }
    }

    /**
     * bean export with params
     */
    public static String beanListExport(List<?> beanList, ExcelExportParams excelExportParams, Function<Dict, String> function, Function<BigExcelWriter, BigExcelWriter> writerFunction) {
        String fileName = OssLocalUtils.fmtXlsxFileName(excelExportParams.getFolderName(), excelExportParams.getFileName());
        BigExcelWriter writer = ExcelUtil.getBigWriter(OssLocalUtils.getOssFileStorageAbsolutePath() + fileName);
        // 处理数据
        List<Map<String, Object>> mapList = new ArrayList<>();
        beanList.forEach(bean -> {
            Map<String, Object> row = new LinkedHashMap<>();
            excelExportParams.getColumns().forEach(columnParams -> row.put(columnParams.getTitle(), formatColumnValue(bean, columnParams, function)));
            mapList.add(row);
        });
        // 设置宽度
        for (int i = 0; i < excelExportParams.getColumns().size(); i++) {
            int width =  excelExportParams.getColumns().get(i).getWidth();
            if (width > 0) {
                writer.setColumnWidth(i, width);
            }
        }
        // 设置样式
        if (null != writerFunction) {
            writerFunction.apply(writer);
        }
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(mapList, true);
        // 关闭writer，释放内存
        writer.close();
        return fileName;
    }

}