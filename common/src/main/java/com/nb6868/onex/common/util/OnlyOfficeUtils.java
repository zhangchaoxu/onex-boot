package com.nb6868.onex.common.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;

/**
 * OnlyOffice工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class OnlyOfficeUtils {

    /**
     * 从文件名中解析文件类型
     * <a href="https://api.onlyoffice.com/docs/docs-api/usage-api/config/">...</a>
     */
    public static String getDocumentTypeByFileName(String fileName) {
        String fileExtName = FileNameUtil.extName(fileName);
        if (StrUtil.isBlank(fileExtName)) {
            return null;
        }
        if (StrUtil.equalsAnyIgnoreCase(fileExtName, "doc", "docx", "wps", "pages", "txt", "docm", "dot", "dotm", "dotx", "epub", "fb2", "fodt", "htm", "html", "hwp", "hwpx", "mht", "mhtml", "odt", "ott", "rtf", "stw", "sxw", "wpt", "xml")) {
            return "word";
        } else if (StrUtil.equalsAnyIgnoreCase(fileExtName, "xls", "xlsx", "csv", "et", "ett", "fods", "numbers", "ods", "ots", "sxc", "xlsb", "xlsm", "xlt", "xltm", "xltx", "xml")) {
            // 注意这里对于xml和word有重复
            return "cell";
        } else if (StrUtil.equalsAnyIgnoreCase(fileExtName, "ppt", "pptx", "dps", "dpt", "fodp", "key", "odp", "otp", "pot", "potm", "potx", "pps", "ppsm", "ppsx", "pptm", "sxi")) {
            return "slide";
        } else if (StrUtil.equalsAnyIgnoreCase(fileExtName, "pdf", "xps", "djvu", "docxf", "oform", "oxps")) {
            return "pdf";
        } else {
            return null;
        }
    }

}
