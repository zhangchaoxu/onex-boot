package com.nb6868.onex.common.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.oss.OssLocalUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * excel工具类
 * @see <a href="https://opensource.afterturn.cn/doc/easypoi.html">easypoi</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class ExcelUtils {

    private final static String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    private final static String FILENAME_XLS_FMT = "attachment;filename={}.xls";
    private final static String FILENAME_XLS_SUFFIX = ".xls";

    // [导出相关]
    /**
     * 通过workbook 下载excel
     */
    public static void downloadExcelFromWorkbook(HttpServletResponse response, String fileName, Workbook workbook) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(CONTENT_TYPE_XLS);
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, StrUtil.format(FILENAME_XLS_FMT, URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())));
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
    }

    /**
     * 下载excel
     *
     * @param response
     * @param exportParams
     * @param fileName
     * @param list
     * @param pojoClass
     */
    public static void downloadExcel(HttpServletResponse response, String fileName, ExportParams exportParams, Class<?> pojoClass, Collection<?> list) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downloadExcelFromWorkbook(response, fileName, workbook);
    }

    /**
     * Excel生成，先sourceList转换成List<targetClass>
     *
     * @param fileName    文件名
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static String genExcelToTarget(String fileName, Collection<?> sourceList, Class<?> targetClass) {
        return genExcelToTarget(fileName, sourceList, targetClass, new ExportParams());
    }

    /**
     * Excel生成，先sourceList转换成List<targetClass>
     *
     * @param fileName    文件名
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static String genExcelToTarget(String fileName, Collection<?> sourceList, Class<?> targetClass, ExportParams exportParams) {
        List<Object> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            Object target;
            try {
                target = targetClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
            }
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        return genExcel(fileName, targetList, targetClass, exportParams);
    }

    public static String genExcel(String fileName, Collection<?> list, Class<?> pojoClass, ExportParams exportParams) {
        try {
            BufferedOutputStream excelFos = FileUtil.getOutputStream(OssLocalUtils.getOssFileStorageAbsolutePath() + fileName + FILENAME_XLS_SUFFIX);
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
            workbook.write(excelFos);
            excelFos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
        return fileName + FILENAME_XLS_SUFFIX;
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response    response
     * @param fileName    文件名
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static void exportExcelToTarget(HttpServletResponse response, String fileName, Collection<?> sourceList, Class<?> targetClass) {
        exportExcelToTarget(response, fileName, sourceList, targetClass, new ExportParams());
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response    response
     * @param fileName    文件名
     * @param sourceList  原数据List
     * @param targetClass 目标对象Class
     */
    public static void exportExcelToTarget(HttpServletResponse response, String fileName, Collection<?> sourceList, Class<?> targetClass, ExportParams exportParams) {
        List<Object> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            Object target;
            try {
                target = targetClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
            }
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        exportExcel(response, fileName, targetList, targetClass, exportParams);
    }

    /**
     * Excel导出
     *
     * @param response  response
     * @param fileName  文件名
     * @param list      数据List
     * @param pojoClass 对象Class
     */
    public static void exportExcel(HttpServletResponse response, String fileName, Collection<?> list, Class<?> pojoClass, ExportParams exportParams) {
        fileName += DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);

        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downloadExcelFromWorkbook(response, fileName, workbook);
    }

    // [导入相关]
    /**
     * Excel 导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     */
    public static <T> List<T> importExcel(MultipartFile multipartFile, Class<?> pojoClass, ImportParams params) {
        try {
            return importExcel(multipartFile.getInputStream(), pojoClass, params);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_IMPORT_ERROR, e);
        }
    }

    public static <T> List<T> importExcel(InputStream inputstream, Class<?> pojoClass, ImportParams params) {
        try {
            return ExcelImportUtil.importExcel(inputstream, pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_IMPORT_ERROR, e);
        }
    }

    public static <T> List<T> importExcel(File file, Class<?> pojoClass, ImportParams params) {
        try {
            return importExcel(Files.newInputStream(file.toPath()), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_IMPORT_ERROR, e);
        }
    }
}
