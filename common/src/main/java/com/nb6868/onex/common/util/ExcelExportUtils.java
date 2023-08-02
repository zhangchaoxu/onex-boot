package com.nb6868.onex.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.Date;

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
     * 格式化文件名
     * 补充yyyyMMddHHmmssSSS,默认同一毫秒内没有同一操作
     */
    public static String fmtXlsxFilename(String foldName, String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return OssLocalUtils.getOssFileStorageAbsolutePath() + fileName + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT) + FILENAME_XLSX_SUFFIX;
        } else {
            return OssLocalUtils.getOssFileStorageAbsolutePath() + (StrUtil.endWith(foldName, FileUtil.FILE_SEPARATOR) ? foldName : foldName + FileUtil.FILE_SEPARATOR) + fileName + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT) + FILENAME_XLSX_SUFFIX;
        }
    }

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
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
    }

}
