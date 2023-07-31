package com.nb6868.onex.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Hutool实现的Excel导出工具
 * see {https://hutool.cn/docs/index.html#/poi/Excel%E7%94%9F%E6%88%90-ExcelWriter}
 * @author Charles zhangchaoxu@gmail.com
 */
public class ExcelExportUtils {

    private final static String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    private final static String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    private final static String FILENAME_XLS_FMT = "attachment;filename={}.xls";
    private final static String FILENAME_XLSX_FMT = "attachment;filename={}.xlsx";
    private final static String FILENAME_XLS_SUFFIX = ".xls";
    private final static String FILENAME_XLSX_SUFFIX = ".xlsx";

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
