package com.nb6868.onexboot.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Jsoup的使用
 * 需要从网页抓取点数据
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsoupTest {

    /**
     * 获取国家码
     */
    @Test
    public void getCountryCode() throws Exception {
        Document document = Jsoup.connect("http://www.loglogo.com/front/countryCode").timeout(3000000).get();
        //像js一样，通过id 获取文章列表元素对象
        Elements code_box = document.getElementsByClass("code_box");
        Elements trs = code_box.select("table").select("tbody").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            System.out.println("--" + tds.get(0).text() + "--" + tds.get(1).text() + "--" + tds.get(2).text() + "--");
        }
    }
}
