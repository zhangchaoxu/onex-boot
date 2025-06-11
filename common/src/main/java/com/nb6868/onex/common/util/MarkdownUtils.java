package com.nb6868.onex.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.List;

/**
 * Markdown工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class MarkdownUtils {

    /**
     * word内容转markdown
     *
     * @param wordFile      word文件本身
     * @param parseImage    处理图片
     * @param imageDir      存储文件的路径
     * @param imageToBase64 文件是否做base64转换
     */
    public static ApiResult<JSONObject> wordToMarkdown(File wordFile, boolean parseImage, String imageDir, boolean imageToBase64) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (!FileUtil.exist(wordFile)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS, "word文件不存在");
        }
        XWPFDocument document;
        try {
            document = new XWPFDocument(new FileInputStream(wordFile));
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS, "word文件读取失败:" + e.getMessage());
        }
        FileUtil.mkdir(imageDir);
        StrJoiner content = new StrJoiner("\n");
        content.setNullMode(StrJoiner.NullMode.IGNORE);
        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                String item = processParagraph((XWPFParagraph) element, parseImage, imageDir, imageToBase64);
                content.append(item);
            } else if (element instanceof XWPFTable) {
                String item = processTable((XWPFTable) element, parseImage, imageDir, imageToBase64);
                content.append(item);
            } else {
                log.info("无法处理的word element类型:{}", element.getElementType());
            }
        }
        return apiResult.success(new JSONObject().set("content", content.toString()));
    }

    /**
     * 解析段落
     */
    private static String processParagraph(XWPFParagraph paragraph, boolean parseImage, String imageDir, boolean imageToBase64) {
        String content = processParagraphContent(paragraph, parseImage, imageDir, imageToBase64);
        if (StrUtil.isBlank(content)) {
            return null;
        }

        // 处理标题和列表样式
        String style = paragraph.getStyle();
        if (StrUtil.startWithIgnoreCase(style, "Heading")) {
            int level = Math.min(Character.getNumericValue(style.charAt(7)), 6);
            StringBuilder heading = new StringBuilder();
            heading.append("#".repeat(Math.max(0, level)));
            heading.append(" ").append(content).append("\n\n");
            return heading.toString();
        } else if (isListParagraph(paragraph)) {
            String listMark = getListMark(paragraph);
            return "* " + listMark + " " + content + "\n";
        } else {
            return content;
        }
    }

    private static String getListMark(XWPFParagraph para) {
        int indentLevel = para.getNumIlvl() != null ? para.getNumIlvl().intValue() : 0;
        // 获取列表编号格式
        String numFmt = para.getNumFmt();
        // 处理有序列表
        if ("decimal".equals(numFmt) || "upperRoman".equals(numFmt)) {
            return " ".repeat(Math.max(0, indentLevel * 4)) + ".";
        } else {
            // 处理无序列表
            String bullet = "bullet".equals(numFmt) ? "•" : "-";
            return " ".repeat(Math.max(0, indentLevel * 4)) + bullet;
        }
    }

    /**
     * 判断是否列表段落
     */
    private static boolean isListParagraph(XWPFParagraph paragraph) {
        return isOrderedList(paragraph) || isUnorderedList(paragraph); // 如果没有找到对应的样式，则不可能是列表段落
    }

    /**
     * 判断是否有序列表段落
     */
    private static boolean isOrderedList(XWPFParagraph paragraph) {
        return StrUtil.equalsAnyIgnoreCase(paragraph.getNumFmt(), "decimal", "upperRoman", "lowerRoman", "upperLetter", "lowerLetter");
    }

    /**
     * 判断是否无序列表段落
     */
    private static boolean isUnorderedList(XWPFParagraph paragraph) {
        return StrUtil.equalsIgnoreCase(paragraph.getNumFmt(), "bullet");
    }

    /**
     * 解析表格
     */
    private static String processTable(XWPFTable table, boolean parseImage, String imageDir, boolean imageToBase64) {
        StrJoiner mdTable = new StrJoiner("");
        mdTable.setNullMode(StrJoiner.NullMode.IGNORE);
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            mdTable.append("|");
            // 处理每个单元格
            for (XWPFTableCell cell : row.getTableCells()) {
                StrJoiner cellContent = new StrJoiner("");
                cellContent.setNullMode(StrJoiner.NullMode.IGNORE);
                // 处理单元格内的段落
                for (XWPFParagraph para : cell.getParagraphs()) {
                    String processParagraphContent = processParagraphContent(para, parseImage, imageDir, imageToBase64);
                    if (StrUtil.isNotBlank(processParagraphContent)) {
                        cellContent.append(processParagraphContent.replace("\n", "<br>"));
                    }
                }
                mdTable.append(cellContent.toString().trim()).append("|");
            }
            mdTable.append("\n");
            // 添加表头分隔线
            if (i == 0) {
                mdTable.append("|");
                mdTable.append(" --- |".repeat(row.getTableCells().size()));
                mdTable.append("\n");
            }
        }
        return mdTable.toString();
    }

    /**
     * 处理段落内容
     */
    private static String processParagraphContent(XWPFParagraph paragraph, boolean parseImage, String imageDir, boolean imageToBase64) {
        StrJoiner sb = new StrJoiner("");
        sb.setNullMode(StrJoiner.NullMode.IGNORE);
        for (XWPFRun run : paragraph.getRuns()) {
            // 处理图片
            for (XWPFPicture picture : run.getEmbeddedPictures()) {
                String imageResult = saveImage(picture, parseImage, imageDir, imageToBase64);
                if (StrUtil.isNotBlank(imageResult)) {
                    sb.append(imageResult).append(" ");
                }
            }
            // 处理文本样式
            String text = run.getText(0);
            if (StrUtil.isEmpty(text)) {
                continue;
            }
            text = applyTextStyles(run, text);
            sb.append(text);
        }
        if (StrUtil.isBlank(sb.toString())) {
            return null;
        }
        String content = sb.toString().trim();
        // 处理有序列表和无序列表
        if (isListParagraph(paragraph)) {
            String listMark = getListMark(paragraph);
            content = "* " + listMark + " " + content;
        }
        return content;
    }

    private static String applyTextStyles(XWPFRun run, String text) {
        if (StrUtil.isEmpty(text)) {
            return null;
        } else if (StrUtil.isBlank(text)) {
            return text;
        }
        if (run.isBold()) {
            text = "**" + text + "**"; // 处理加粗
        }
        if (run.isItalic()) {
            text = "*" + text + "*"; // 处理斜体
        }
        if (run.getUnderline() != UnderlinePatterns.NONE) {
            text = "__" + text + "__"; // 处理下划线
        }
        return text;
    }

    /**
     * 保存图片
     *
     * @param picture       图片内容
     * @param imageDir      保存的图片路径
     * @param imageToBase64 图片是否转base64
     */
    private static String saveImage(XWPFPicture picture, boolean parseImage, String imageDir, boolean imageToBase64) {
        if (!parseImage) {
            return null;
        }
        XWPFPictureData picData = picture.getPictureData();
        String fileName = IdUtil.fastSimpleUUID() + "." + picData.suggestFileExtension();
        if (imageToBase64) {
            File output = new File(imageDir, fileName);
            // 文件写入
            FileUtil.writeBytes(picData.getData(), output);
            return StrUtil.format("![{}](data:{}:base64,{})", fileName, FileUtil.getMimeType(fileName), Base64.encode(picData.getData()));
        } else {
            // 文件写入
            try {
                FileUtil.writeBytes(picData.getData(), new File(imageDir, fileName));
            } catch (Exception e) {
                log.error("文件写入失败", e);
            }
            return "![" + fileName + "](" + imageDir + "/" + fileName + ")";
        }
    }

}
