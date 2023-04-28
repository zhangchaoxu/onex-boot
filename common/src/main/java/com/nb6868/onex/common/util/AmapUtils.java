package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 高德 Web服务 API
 * 高德有应该隐藏参数s=rsv3,可以将webApi的接口当前端接口用,避免USERKEY_PLAT_NOMATCH的问题
 * <a href="https://lbs.amap.com/api/webservice/gettingstarted">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AmapUtils {

    private static final String BASE_URL = "https://restapi.amap.com";

    /**
     * 地理编码
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static JSONObject geocodeGeo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/geocode/geo", paramMap);
            log.info("Amap.geocodeGeo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.geocodeGeo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 逆地理编码
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static JSONObject geocodeRegeo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/geocode/regeo", paramMap);
            log.info("Amap.geocodeRegeo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.geocodeRegeo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 距离测量
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static JSONObject distance(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/distance", paramMap);
            log.info("Amap.distance=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.distance error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 步行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static JSONObject directionWalking(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/direction/walking", paramMap);
            log.info("Amap.directionWalking=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionWalking error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 驾车路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static JSONObject directionDriving(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/direction/driving", paramMap);
            log.info("Amap.directionDriving=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionDriving error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 骑行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static JSONObject directionBicycling(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/direction/bicycling", paramMap);
            log.info("Amap.directionBicycling=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionBicycling error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 公交路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static JSONObject directionTransit(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/direction/transit/integrated", paramMap);
            log.info("Amap.directionTransit=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionTransit error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 静态地图
     * <a href="https://lbs.amap.com/api/webservice/guide/api/staticmaps">...</a>
     */
    public static String staticmap(JSONObject paramMap) {
        return StrUtil.format(BASE_URL + "/v3/staticmap?{}", HttpUtil.toParams(paramMap));
    }

    /**
     * 天气查询
     * <a href="https://lbs.amap.com/api/webservice/guide/api/weatherinfo">...</a>
     */
    public static JSONObject weatherInfo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/weather/weatherInfo", paramMap);
            log.info("Amap.weatherInfo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.weatherInfo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * IP定位
     * <a href="https://lbs.amap.com/api/webservice/guide/api/ipconfig">...</a>
     */
    public static JSONObject ip(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/ip", paramMap);
            log.info("Amap.ip=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.ip error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 坐标转换
     * <a href="https://lbs.amap.com/api/webservice/guide/api/convert">...</a>
     */
    public static JSONObject coordinateConvert(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/assistant/coordinate/convert", paramMap);
            log.info("Amap.coordinateConvert=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.coordinateConvert error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 行政区域查询
     * <a href="https://lbs.amap.com/api/webservice/guide/api/district">...</a>
     */
    public static JSONObject district(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/config/district", paramMap);
            log.info("Amap.district=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.district error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 输入提示
     * <a href="https://lbs.amap.com/api/webservice/guide/api/inputtips">...</a>
     */
    public static JSONObject inputtips(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/assistant/inputtips", paramMap);
            log.info("Amap.inputtips=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.inputtips error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 搜索POI
     * <a href="https://lbs.amap.com/api/webservice/guide/api/search">...</a>
     */
    public static JSONObject poi(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v3/place/text", paramMap);
            log.info("Amap.poi=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.poi error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 搜索POI 2.0
     * <a href="https://lbs.amap.com/api/webservice/guide/api/newpoisearch">...</a>
     */
    public static JSONObject poiV2(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get(BASE_URL + "/v5/place/text", paramMap);
            log.info("Amap.poiV2=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.poiV2 error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

}
