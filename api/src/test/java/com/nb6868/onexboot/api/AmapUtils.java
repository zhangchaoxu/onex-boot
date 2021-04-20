package com.nb6868.onexboot.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

/**
 * 高德地图
 *
 * @author Charles zhangchaoxu@gmail.com
 */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AmapUtils {

    @Test
    void getPoi() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://amap.com/service/poiInfo?query_type=TQUERY&pagesize=10&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&addr_poi_merge=true&is_classify=true&city={1}&keywords={2}";
        System.out.println(url);
        String result = restTemplate.getForObject(url, String.class, "330200", "锦诚花园");
        System.out.println(result);
    }

}
