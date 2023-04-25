package com.nb6868.onex.common.util;

import cn.afterturn.easypoi.cache.WordCache;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;
import cn.afterturn.easypoi.word.parse.ParseWord07;
import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Word模板工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class WordTplUtils {

    /**
     * 导出参数
     */
    @Data
    @AllArgsConstructor
    public static class ExportParams {
        // 模板路径
        private String url;
        // 模板参数内容
        private Map<String, Object> params;
        // 是否分页
        private boolean pageBreak;
        // 图片类型,负数不处理
        private int pictureType;
    }

    /**
     * 多模板合并
     */
    public static XWPFDocument exportWord07Merge(List<ExportParams> exportParamsList) throws Exception {
        if (CollUtil.isEmpty(exportParamsList)) {
            return null;
        } else if (exportParamsList.size() == 1) {
            return new ParseWord07().parseWord(exportParamsList.get(0).getUrl(), exportParamsList.get(0).getParams());
        } else {
            ParseWord07 parseWord07 = new ParseWord07();
            MyXWPFDocument doc = WordCache.getXWPFDocument(exportParamsList.get(0).getUrl());
            parseWord07.parseWord(doc, exportParamsList.get(0).getParams());
            doc.createParagraph().setPageBreak(exportParamsList.get(0).isPageBreak());
            for (int i = 1; i < exportParamsList.size(); i++) {
                MyXWPFDocument tempDoc = WordCache.getXWPFDocument(exportParamsList.get(i).getUrl());
                parseWord07.parseWord(tempDoc, exportParamsList.get(i).getParams());
                tempDoc.createParagraph().setPageBreak(exportParamsList.get(i).isPageBreak());
                if (exportParamsList.get(i).getPictureType() < 0) {
                    // 保留原方法
                    doc.getDocument().addNewBody().set(tempDoc.getDocument().getBody());
                } else {
                    // 原方法在循环的时候，图片都会显示第一页的内容
                    docMerge(doc, tempDoc, exportParamsList.get(i).getPictureType());
                }
            }
            return doc;
        }
    }

    /**
     * 修复WordExportUtil.exportWord07对于图片处理的异常
     * 同时增加pageBreak的支持
     */
    public static XWPFDocument exportWord07Fixed(String url, List<Map<String, Object>> list, boolean pageBreak, int pictureType) throws Exception {
        ParseWord07 parseWord07 = new ParseWord07();
        if (list == null || list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return parseWord07.parseWord(url, list.get(0));
        } else {
            MyXWPFDocument doc = WordCache.getXWPFDocument(url);
            parseWord07.parseWord(doc, list.get(0));
            //插入分页
            doc.createParagraph().setPageBreak(true);
            for (int i = 1; i < list.size(); i++) {
                MyXWPFDocument tempDoc = WordCache.getXWPFDocument(url);
                parseWord07.parseWord(tempDoc, list.get(i));
                tempDoc.createParagraph().setPageBreak(pageBreak);
                if (pictureType < 0) {
                    // 保留原方法
                    doc.getDocument().addNewBody().set(tempDoc.getDocument().getBody());
                } else {
                    docMerge(doc, tempDoc, pictureType);
                }
            }
            return doc;
        }
    }

    /**
     * 文档拼接
     * see https://gitee.com/lemur/easypoi/issues/I41WMF
     *
     * @param src           被添加的文档
     * @param append        添加的文档
     * @param pictureFormat 图片格式，Document.PICTURE_TYPE_PNG/Document.PICTURE_TYPE_JPG
     */
    public static void docMerge(XWPFDocument src, XWPFDocument append, int pictureFormat) throws Exception {
        CTBody src1Body = src.getDocument().getBody();
        CTBody src2Body = append.getDocument().getBody();
        List<XWPFPictureData> allPictures = append.getAllPictures();
        // 记录图片合并前及合并后的ID
        Map<String, String> map = new HashMap<>();
        for (XWPFPictureData picture : allPictures) {
            String before = append.getRelationId(picture);
            // 将原文档中的图片加入到目标文档中
            String after = src.addPictureData(picture.getData(), pictureFormat);
            map.put(before, after);
        }
        bodyMerge(src1Body, src2Body, map);
    }

    private static void bodyMerge(CTBody src, CTBody append, Map<String, String> map) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);

        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        if (map != null && !map.isEmpty()) {
            // 对xml字符串中图片ID进行替换
            for (Map.Entry<String, String> set : map.entrySet()) {
                addPart = addPart.replace(set.getKey(), set.getValue());
            }
        }
        // 将两个文档的xml内容进行拼接
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        src.set(makeBody);
    }

}
