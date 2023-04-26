package com.nb6868.onex.common.poi;

import cn.afterturn.easypoi.cache.WordCache;
import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;
import cn.afterturn.easypoi.word.parse.ParseWord07;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.util.WordTplUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * see http://doc.wupaas.com/docs/easypoi/easypoi-1c10lkljso2d7
 */
@DisplayName("Word模板测试")
@Slf4j
public class WordTplTest {

    private Map<String, Object> initParams() {
        // 构造模板数据
        Map<String, Object> params = new HashMap<>();
        params.put("sjly", "天眼查");
        params.put("cjsj", DateUtil.today());
        JSONObject firm = new JSONObject();
        firm.set("name", "宁波得力电子商务有限公司");
        firm.set("code", "91330226662065356C");
        firm.set("address", "浙江省宁海县宁波市浙江省宁波市宁海县跃龙街道世贸中心1幢1号2206室");
        firm.set("scope", "办公用品、工艺品、服装鞋帽、电子产品、办公设备、日用百货、五金产品、电气设备、一般劳动防护用品的网上销售及批发、零售；道路货物运输，国内水路运输，国际道路运输，公共航空运输业务，铁路运输（以上凭有效许可证经营）。");
        firm.set("leader", "娄甫君");
        firm.set("money", "1000万人民币");
        params.put("firm", firm);

        ImageEntity gzImg = new ImageEntity();
        gzImg.setHeight(120);
        gzImg.setWidth(120);
        gzImg.setUrl("https://gss0.baidu.com/7Po3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/6a63f6246b600c336098899a1a4c510fd8f9a1fb.jpg");
        gzImg.setType(ImageEntity.URL);
        gzImg.setLocationType(ImageEntity.EMBED);
        // 公章
        params.put("gzImg", gzImg);
        return params;
    }

    private List<Map<String, Object>> initListParams() {
        // 构造模板数据
        List<Map<String, Object>> list = new ArrayList<>();

        File file = HttpUtil.downloadFileFromUrl("https://pic.ntimg.cn/20110325/2457331_234414382000_2.png", "C:\\Workspaces\\coderTest\\");
        ImageEntity image1 = new ImageEntity();
        // 计算宽高，保持宽高比
        image1.setHeight(Img.from(file).getImg().getHeight(null) * 500 / Img.from(file).getImg().getWidth(null));
        image1.setWidth(500);
        //image1.setData(FileUtil.readBytes(file));
        image1.setUrl("https://pic.ntimg.cn/20110325/2457331_234414382000_2.png");
        image1.setType(ImageEntity.URL);

        ImageEntity image2 = new ImageEntity();
        image2.setHeight(200);
        image2.setWidth(500);
        image2.setUrl("https://cdn.tianyancha.com/web-require-js/public/images/footer-app-qrcode-1_optm.png");
        image2.setType(ImageEntity.URL);

        ImageEntity image3 = new ImageEntity();
        image3.setHeight(500);
        image3.setWidth(500);
        image3.setUrl("https://cdn.tianyancha.com/web-require-js/public/images/footer-wx-qrcode-1_optm.png");
        image3.setType(ImageEntity.URL);

        Map<String, Object> item1 = new HashMap<>();
        item1.put("index", 1);
        item1.put("url", "https://mall.jd.com/index-177232.html");
        item1.put("platform", "京东");
        item1.put("name", "得力插座官方旗舰店");
        item1.put("img", image1);
        list.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("index", 2);
        item2.put("url", "https://mall.jd.com/index-942013.html");
        item2.put("platform", "京东");
        item2.put("name", "得力文具旗舰店");
        item2.put("img", image2);
        list.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("index", 3);
        item3.put("url", "https://shop262603802.taobao.com/");
        item3.put("platform", "淘宝");
        item3.put("name", "得力插座官方旗舰店");
        item3.put("img", image3);
        list.add(item3);

        return list;
    }

    @Test
    @DisplayName("模板拼接,将一个模板+循环模板+列表模板拼接起来")
    void mergeDoc() {
        // 模板路径，支持绝对路径或resources文件夹下的路径
        String tplPath1 = "poi/word_tpl_1.docx";
        String tplPath2 = "poi/word_tpl_2.docx";
        String tplPath3 = "poi/word_tpl_3.docx";

        // 构造模板数据
        Map<String, Object> params = initParams();
        List<Map<String, Object>> paramsList = initListParams();
        params.put("shopList", paramsList);

        XWPFDocument doc  = null;
        FileOutputStream fos = null;
        // 页码
        String filePath = "测试报告-" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".docx";
        try {
            List<WordTplUtils.ExportParams> exportParamsList = new ArrayList<>();
            // 头部
            exportParamsList.add(new WordTplUtils.ExportParams(tplPath1, params, false, Document.PICTURE_TYPE_PNG));
            // 中间
            paramsList.forEach(stringObjectMap -> {
                // 循环
                exportParamsList.add(new WordTplUtils.ExportParams(tplPath2, stringObjectMap, false, Document.PICTURE_TYPE_PNG));
            });
            // 尾部
            exportParamsList.add(new WordTplUtils.ExportParams(tplPath3, params, false, Document.PICTURE_TYPE_PNG));

            doc = WordTplUtils.exportWord07Merge(exportParamsList);
            fos = new FileOutputStream("C:\\Workspaces\\coderTest\\" + filePath);
            doc.write(fos);
        } catch (Exception e) {
            log.error("报告文件生成失败", e);
        } finally {
            IoUtil.close(doc);
            IoUtil.close(fos);
        }
    }

    @Test
    @DisplayName("模板循环")
    void genListDoc() {
        // 模板路径，支持绝对路径或resources文件夹下的路径
        String tplPath2 = "poi/word_tpl_2.docx";

        // 构造模板数据
        List<Map<String, Object>> paramsList = initListParams();

        FileOutputStream fos = null;
        // 页码
        String filePath = "测试报告-" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".docx";
        try {
            // XWPFDocument doc =  WordExportUtil.exportWord07(tplPath2, paramsList);
            ParseWord07 parseWord07 = new ParseWord07();
            // 头部
            MyXWPFDocument doc = WordCache.getXWPFDocument(tplPath2);
            parseWord07.parseWord(doc, paramsList.get(0));
            doc.createParagraph().setPageBreak(false);
            // 中间循环
            for (int i = 1; i < paramsList.size(); i++) {
                MyXWPFDocument tempDoc = WordCache.getXWPFDocument(tplPath2);
                parseWord07.parseWord(tempDoc, paramsList.get(i));
                tempDoc.createParagraph().setPageBreak(false);
                // doc.getDocument().addNewBody().set(tempDoc.getDocument().getBody());
                WordTplUtils.docMerge(doc, tempDoc, Document.PICTURE_TYPE_PNG);
            }
            fos = new FileOutputStream("C:\\Workspaces\\coderTest\\" + filePath);
            doc.write(fos);
        } catch (Exception e) {
            log.error("报告文件生成失败", e);
        } finally {
            IoUtil.close(fos);
        }
    }

    /**
     * 修改表格的字体
     *
     * XWPFTable 从XWPFDocument.getTables获取
     * 需要添加依赖org.apache.poi:ooxml-schemas:1.4
     *
     * see {https://blog.csdn.net/LaneDu/article/details/108637439}
     */
    public static void changeTableFont(XWPFTable sourceTable, String fontName) {
        //获取所有的行数
        int size = sourceTable.getRows().size();
        for (int i = 1; i < size; i++) {
            //获取每一行
            XWPFTableRow row = sourceTable.getRow(i);
            List<XWPFTableCell> cellList = row.getTableCells();
            //获取每一行空格的总个数
            final int cellSize = cellList.size();
            //进行循环遍历修改每一个空格的样式,标题行不修改样式
            for (int j = 0; j < cellSize && cellSize > 1; j++) {
                //全网独一份，根据模板的样式设置总的样式
                CTFonts tmpFonts = row.getCell(j).getParagraphs().get(0).getRuns().get(0).getCTR().getRPr().getRFonts();
                // tmpFonts.setHint(tmpFonts0.getHint());
                tmpFonts.setAscii(fontName);
                tmpFonts.setEastAsia(fontName);
                tmpFonts.setHAnsi(fontName);
                tmpFonts.setCs("Arial");
                tmpFonts.unsetAsciiTheme();
                tmpFonts.unsetEastAsiaTheme();
                tmpFonts.unsetHAnsiTheme();
            }
        }
    }

}
