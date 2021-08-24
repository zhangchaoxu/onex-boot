package com.nb6868.onex.api.common.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * excel工具类
 * [easypoi](https://opensource.afterturn.cn/doc/easypoi.html)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ExcelUtils {

    // [导出相关]
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
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + ".xls");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
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

        exportExcel(response, fileName, targetList, targetClass);
    }

    /**
     * Excel导出
     *
     * @param response  response
     * @param fileName  文件名
     * @param list      数据List
     * @param pojoClass 对象Class
     */
    public static void exportExcel(HttpServletResponse response, String fileName, Collection<?> list, Class<?> pojoClass) {
        fileName += DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, list);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + ".xls");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_EXPORT_ERROR, e);
        }
    }

    // [导入相关]
    /**
     * Excel 导入 数据源IO流,不返回校验结果 导入 字段类型 Integer,Long,Double,Date,String,Boolean
     *
     * @param multipartFile
     * @param pojoClass
     * @param params
     * @return
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
            return importExcel(new FileInputStream(file), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.EXCEL_IMPORT_ERROR, e);
        }
    }
}
