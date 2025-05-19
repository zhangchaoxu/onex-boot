package com.nb6868.onex.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.oss.OssLocalUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Hutool实现的Excel导出工具
 * see {<a href="https://hutool.cn/docs/index.html#/poi/Excel%E7%94%9F%E6%88%90-ExcelWriter">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
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
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format(FILENAME_XLSX_FMT, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
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
     * @param bean               实体bean
     * @param column             列定义
     * @param cellFormatFunction cell格式化方法
     */
    public static Object formatColumnValue(ExcelWriter excelWriter, Object bean, ExcelExportParams.ColumnParams column, int index, Function<Dict, String> cellFormatFunction) {
        String fmt = column.getFmt();
        try {
            if (StrUtil.isBlank(fmt)) {
                // 非String类型，转String会ClassCastException
                Object pObject = BeanUtil.getProperty(bean, column.getProperty());
                if (pObject == null || (pObject instanceof String && StrUtil.isEmpty(pObject.toString()))) {
                    return column.getEmptyToDefault();
                } else {
                    if (pObject instanceof String) {
                        // 限制长度32767
                        return StrUtil.sub((String) pObject, 0, 32767);
                    } else {
                        return pObject;
                    }
                }
            } else {
                String pValue = "";
                if ("time".equalsIgnoreCase(fmt)) {
                    // 时间格式化
                    Object pObject = BeanUtil.getProperty(bean, column.getProperty());
                    if (pObject instanceof Long) {
                        long timestamp = (long) pObject;
                        // tofix 理论上无法通过数值大小来区分秒和毫秒
                        pValue = DateUtil.format(DateUtil.date((timestamp < 10000000000L ? 1000 : 1) * timestamp), column.getTimeFormat());
                    } else if (pObject instanceof Date) {
                        pValue = DateUtil.format((Date) pObject, column.getTimeFormat());
                    } else if (pObject instanceof LocalDateTime) {
                        pValue = DateUtil.format((LocalDateTime) pObject, column.getTimeFormat());
                    }
                } else if ("invoke".equalsIgnoreCase(fmt)) {
                    // 反射,执行invokeMethod 若空，则执行getProperty
                    String invokeMethod = StrUtil.emptyToDefault(column.getInvokeMethod(), "get" + StrUtil.upperFirst(column.getProperty()));
                    pValue = ReflectUtil.invoke(bean, invokeMethod);
                } else if ("index".equalsIgnoreCase(fmt)) {
                    // 序号
                    pValue = String.valueOf(index);
                } else if ("enum".equalsIgnoreCase(fmt)) {
                    // 枚举
                    Object pObject = BeanUtil.getProperty(bean, column.getProperty());
                    return column.getEnmuMap().get(pObject.toString());
                } else if (cellFormatFunction != null) {
                    pValue = cellFormatFunction.apply(Dict.create().set("bean", bean).set("column", column));
                }
                if (column.isLink()) {
                    // 获得链接地址
                    String linkValue = ReflectUtil.invoke(bean, "get" + StrUtil.upperFirst(column.getLinkProperty()));
                    return excelWriter.createHyperlink(HyperlinkType.URL, linkValue, StrUtil.emptyToDefault(pValue, column.getEmptyToDefault()));
                } else {
                    // 非链接，直接输出文本, 限制长度32767
                    return StrUtil.sub(StrUtil.emptyToDefault(pValue, column.getEmptyToDefault()), 0, 32767);
                }
            }
        } catch (Exception e) {
            // 遇到问题返回异常,便于从结果中发现问题
            log.error("转换excel的column失败", e);
            return StrUtil.nullToDefault(column.getErrorDefaultMsg(), e.getMessage());
        }
    }

    /**
     * bean export with params
     */
    @SuppressWarnings("unchecked")
    public static String beanListExport(List<?> beanList, ExcelExportParams excelExportParams) {
        return beanListExport(beanList, excelExportParams, null, null, null);
    }

    public static String beanListExport(List<?> beanList, ExcelExportParams excelExportParams, Function<Dict, String> cellFormatFunction, Function<ExcelWriter, ExcelWriter> beforeWriterFunction, Function<ExcelWriter, ExcelWriter> afterWriterFunction) {
        String fileName = OssLocalUtils.fmtXlsxFileName(excelExportParams.getFolderName(), excelExportParams.getFileName());
        BigExcelWriter writer = ExcelUtil.getBigWriter(getFileStoragePath(fileName));
        int columnSize = 0;// 列数
        // 处理数据
        List<Map<String, Object>> mapList;
        if ("raw".equalsIgnoreCase(excelExportParams.getRenderType())) {
            mapList = (List<Map<String, Object>>) beanList;
            if (!mapList.isEmpty()) {
                columnSize = mapList.get(0).size();
            }
        } else {
            mapList = new ArrayList<>();
            beanList.forEach(bean -> {
                Map<String, Object> row = new LinkedHashMap<>();
                excelExportParams.getColumns().forEach(columnParams -> {
                    // 按行插入内容
                    row.put(columnParams.getTitle(), formatColumnValue(writer, bean, columnParams, mapList.size() + 1, cellFormatFunction));
                });
                mapList.add(row);
            });
            // 设置宽度
            for (int i = 0; i < excelExportParams.getColumns().size(); i++) {
                int width = excelExportParams.getColumns().get(i).getWidth();
                if (width > 0) {
                    writer.setColumnWidth(i, width);
                }
            }
            columnSize = excelExportParams.getColumns().size();
        }
        // 设置样式,在写入数据之前
        if (StrUtil.isNotBlank(excelExportParams.getHeaderTitle()) && columnSize > 0) {
            // 合并单元格后的标题行，使用默认标题样式
            writer.merge(columnSize - 1, excelExportParams.getHeaderTitle());
        }
        if (null != beforeWriterFunction) {
            beforeWriterFunction.apply(writer);
        }
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(mapList, true);
        // 设置样式,在写入数据之后
        if (null != afterWriterFunction) {
            afterWriterFunction.apply(writer);
        }
        // 设置行高,需要在write后，
        if (excelExportParams.getHeaderHeight() > 0 && writer.getRowCount() > 0) {
            writer.setRowHeight(0, excelExportParams.getHeaderHeight());
        }
        if (excelExportParams.getHeaderHeight() > 0 && writer.getRowCount() > 1) {
            for (int i = 1; i < writer.getRowCount(); i++) {
                writer.setRowHeight(i, excelExportParams.getRowHeight());
            }
        }
        // 关闭writer，释放内存
        writer.close();
        return fileName;
    }

    /**
     * 获得文件存储路径
     *
     * @param fileName 文件名称
     */
    public static String getFileStoragePath(String fileName) {
        return OssLocalUtils.getOssFileStorageAbsolutePath() + fileName;
    }

    /**
     * 获得文件请求路径
     *
     * @param fileName 文件名称
     */
    public static String getFileRequestPath(String fileName) {
        return OssLocalUtils.getOssRequestPrefix() + fileName;
    }

}
