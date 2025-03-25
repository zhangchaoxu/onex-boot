package com.nb6868.onex.common.poi;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

/**
 * see https://deepoove.com/poi-tl/
 */
@DisplayName("PoiTlWord模板测试")
@Slf4j
public class PoiTlTest {

    @Test
    @DisplayName("渲染chart")
    void renderChart() throws Exception {
        String rootPath = "E:\\Workspaces\\nb6868\\onex-boot\\common\\src\\test\\resources\\";
        ConfigureBuilder builder = Configure.builder();
        // 使用SpringEL
        // builder.useSpringEL(false);
        // 对标签不作任何处理 https://deepoove.com/poi-tl/#_%E6%A0%87%E7%AD%BE%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E4%B8%8D%E5%90%88%E6%B3%95
        // builder.setValidErrorHandler(new Configure.DiscardHandler());
        // 封装模板渲染数据
        Map<String, Object> includesData = new HashMap<>();
        // 组装父模板中chart
        includesData.put("parentChart", Charts
                .ofMultiSeries("电商运营指数", new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"})
                .addSeries("指标A（家)", new Number[]{1, 2, 3, 4, 5, 6})
                .addSeries("指标B（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}).create());
        Map<String, Object> sub1RenderModel = new HashMap<>();
        // 组装子模板chart1
        //参数为数组
        ChartMultiSeriesRenderData bar = new ChartMultiSeriesRenderData();
        bar.setChartTitle("我是统计标题");
        bar.setCategories(new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"});
        List<SeriesRenderData> seriesRenderDatas = new ArrayList<>();
        seriesRenderDatas.add(new SeriesRenderData("监测量（家)", new Number[]{1, 2, 3, 4, 5, 6}));
        seriesRenderDatas.add(new SeriesRenderData("违法量（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}));
        bar.setSeriesDatas(seriesRenderDatas);
        sub1RenderModel.put("subChart", bar);
        // sub1RenderModel.put("subtitle", "我是subtitle");
        sub1RenderModel.put("mock", "我是mock");

        /*sub1RenderModel.put("sub1Chart", Charts
                .ofMultiSeries("电商运营指数", new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"})
                .addSeries("指标A（家)", new Number[]{1, 2, 3, 4, 5, 6})
                .addSeries("指标B（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}).create());*/
        includesData.put("sub1", Includes.ofLocal(rootPath + "poi/sub1.docx").setRenderModel(sub1RenderModel).create());
        // 自定义图片标签渲染策略
        XWPFTemplate template = XWPFTemplate.compile(rootPath + "poi/parent.docx", builder.build()).render(includesData);
        template.writeToFile(rootPath + "poi/chart_result.docx");
    }

    @Test
    @DisplayName("渲染chart")
    void renderChart2() throws Exception {
        String rootPath = "E:\\Workspaces\\nb6868\\onex-boot\\common\\src\\test\\resources\\";
        ConfigureBuilder builder = Configure.builder();
        // 使用SpringEL
        //builder.useSpringEL(false);
        // 对标签不作任何处理 https://deepoove.com/poi-tl/#_%E6%A0%87%E7%AD%BE%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B%E4%B8%8D%E5%90%88%E6%B3%95
        //builder.setValidErrorHandler(new Configure.DiscardHandler());
        // 封装模板渲染数据
        Map<String, Object> includesData = new HashMap<>();
        // 组装父模板中chart
        includesData.put("parentChart", Charts
                .ofMultiSeries("电商运营指数", new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"})
                .addSeries("指标A（家)", new Number[]{1, 2, 3, 4, 5, 6})
                .addSeries("指标B（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}).create());
        Map<String, Object> sub1RenderModel = new HashMap<>();
        // 组装子模板chart1
        sub1RenderModel.put("sub1Chart", Charts
                .ofMultiSeries("电商运营指数", new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"})
                .addSeries("指标A（家)", new Number[]{1, 2, 3, 4, 5, 6})
                .addSeries("指标B（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}).create());
        includesData.put("sub1", Includes.ofLocal(rootPath + "poi/sub1.docx").setRenderModel(sub1RenderModel).create());
        // 组装子模板chart2
        Map<String, Object> sub2RenderModel = new HashMap<>();
        Map<String, Object> sub2ChartRenderModel = new HashMap<>();
        sub2ChartRenderModel.put("sub2Chart", Charts
                .ofMultiSeries("电商运营指数", new String[]{"京东", "淘宝", "天猫", "阿里巴巴", "抖音", "快手"})
                .addSeries("指标A（家)", new Number[]{1, 2, 3, 4, 5, 6})
                .addSeries("指标B（条）", new Number[]{1.2, 2.3, 3.2, 3, 5.2, 6.1}).create());
        DocxRenderData docxRenderData = new DocxRenderData(new File(rootPath + "poi/sub2_chart.docx"), Arrays.asList(sub2ChartRenderModel));
        sub2RenderModel.put("sub2sub", docxRenderData);
        //sub2RenderModel.put("sub2sub", Includes.ofLocal(rootPath + "poi/sub2_chart.docx").setRenderModel(sub2ChartRenderModel).create());
        includesData.put("sub2", Includes.ofLocal(rootPath + "poi/sub2.docx").setRenderModel(sub2RenderModel).create());
        // includesData.put("sub2", new DocxRenderData(new File(rootPath + "poi/sub2.docx"), params));
        // 子模板23456
        // 自定义图片标签渲染策略
        XWPFTemplate template = XWPFTemplate.compile(rootPath + "poi/parent.docx", builder.build()).render(includesData);
        template.writeToFile(rootPath + "poi/chart_result.docx");
    }

    @Test
    @DisplayName("带图片和图标的模板")
    void testAnimal() throws Exception {
        Map<String, Object> elephant = new HashMap<String, Object>() {
            {
                put("name", "大象");
                put("chart",
                        Charts.ofMultiSeries("大象生存现状", new String[]{"2018年", "2019年", "2020年"})
                                .addSeries("成年象", new Integer[]{500, 600, 700})
                                .addSeries("幼象", new Integer[]{200, 300, 400})
                                .addSeries("全部", new Integer[]{700, 900, 1100})
                                .create());
            }
        };
        Map<String, Object> giraffe = new HashMap<String, Object>() {
            {
                put("name", "长颈鹿");
                put("picture", Pictures.ofLocal("src/test/resources/poi/lu.png").size(100, 120).create());
                put("chart",
                        Charts.ofMultiSeries("长颈鹿生存现状", new String[]{"2018年", "2019年", "2020年"})
                                .addSeries("成年鹿", new Integer[]{500, 600, 700})
                                .addSeries("幼鹿", new Integer[]{200, 300, 400})
                                .create());

            }
        };
        List<Map<String, Object>> animals = Arrays.asList(elephant, giraffe);
        XWPFTemplate.compile("src/test/resources/poi/animal.docx").render(new HashMap<String, Object>() {
            {
                put("animals", animals);
            }
        }).writeToFile("target/out_example_animal.docx");
    }

}
