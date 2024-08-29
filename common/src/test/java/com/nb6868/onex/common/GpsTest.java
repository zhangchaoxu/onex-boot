package com.nb6868.onex.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.util.AmapApiUtils;
import com.nb6868.onex.common.util.GpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DisplayName("Gps测试")
@Slf4j
public class GpsTest {

    @DisplayName("经纬度计算")
    @Test
    void testGpsCalc() {
        GpsUtils.LngLat lngLat1 = new GpsUtils.LngLat(121.48705, 29.964235);
        GpsUtils.LngLat lngLat2 = new GpsUtils.LngLat(121.49705, 29.963235);

        log.error("calBD09toGCJ02={}", GpsUtils.calBD09toGCJ02(lngLat1));
        log.error("calBD09toWGS84={}", GpsUtils.calBD09toWGS84(lngLat1));
        log.error("calWGS84toGCJ02={}", GpsUtils.calWGS84toGCJ02(lngLat1));
        log.error("calWGS84toBD09={}", GpsUtils.calWGS84toBD09(lngLat1));
        log.error("calGCJ02toBD09={}", GpsUtils.calGCJ02toBD09(lngLat1));
        log.error("calGCJ02toWGS84={}", GpsUtils.calGCJ02toWGS84(lngLat1));
        log.error("getDistance={}", GpsUtils.getDistance(lngLat1, lngLat2));
    }

    private final static String AMAP_KEY = "";

    @DisplayName("高德测试")
    @Test
    void testAmap() {
        AmapApiUtils amapClient = new AmapApiUtils();

        GpsUtils.LngLat lngLat = new GpsUtils.LngLat(121.570626, 29.90893);
        JSONObject geocodeRegeoForm = new JSONObject()
                .set("radius", 200)
                .set("batch", false)
                .set("extensions", "base")
                .set("key", AMAP_KEY)
                .set("location", lngLat.toString());
        ApiResult<JSONObject> resp = amapClient.geocodeRegeo(geocodeRegeoForm);
        log.error("geocodeRegeo={}", resp);

        GpsUtils.LngLat gcj02 = GpsUtils.calWGS84toGCJ02(lngLat);
        JSONObject geocodeRegeoForm2 = new JSONObject()
                .set("radius", 200)
                .set("batch", false)
                .set("extensions", "base")
                .set("key", AMAP_KEY)
                .set("location", gcj02.toString());
        ApiResult<JSONObject> resp2 = amapClient.geocodeRegeo(geocodeRegeoForm2);
        log.error("geocodeRegeo={}", resp2);
    }

    @DisplayName("kmeans聚合")
    @Test
    void testGpsKmeans() {
        List<GpsUtils.LngLat> dataset = new ArrayList<>();
        List<Map<String, Object>> list = ExcelUtil.getReader("20230704(1).xlsx").readAll();
        for (Map<String, Object> map : list) {
            Double lng = MapUtil.getDouble(map, "_经度");
            Double lat = MapUtil.getDouble(map, "_纬度");
            if (ObjectUtil.isNotEmpty(lat) && ObjectUtil.isNotEmpty(lng)) {
                GpsUtils.LngLat point = new GpsUtils.LngLat();
                point.setLng(lng);
                point.setLat(lat);
                dataset.add(point);
            }
        }
        JSONArray result = GpsUtils.kmeansClusterResult(dataset, 200);
        log.error("result=" + result);
    }

}
