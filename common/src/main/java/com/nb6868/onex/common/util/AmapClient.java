package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 高德 Web服务 API
 * https://lbs.amap.com/api/webservice/gettingstarted
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class AmapClient {

    /**
     * 地理编码
     * see https://lbs.amap.com/api/webservice/guide/api/georegeo
     */
    public JSONObject geocodeGeo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/geocode/geo", paramMap);
            log.info("Amap.geocodeGeo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.geocodeGeo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 逆地理编码
     * see https://lbs.amap.com/api/webservice/guide/api/georegeo
     */
    public JSONObject geocodeRegeo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/geocode/regeo", paramMap);
            log.info("Amap.geocodeRegeo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.geocodeRegeo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 距离测量
     * see https://lbs.amap.com/api/webservice/guide/api/direction
     */
    public JSONObject distance(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/distance", paramMap);
            log.info("Amap.distance=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.distance error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 步行路径规划
     * see https://lbs.amap.com/api/webservice/guide/api/direction
     */
    public JSONObject directionWalking(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/direction/walking", paramMap);
            log.info("Amap.directionWalking=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionWalking error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 驾车路径规划
     * see https://lbs.amap.com/api/webservice/guide/api/direction
     */
    public JSONObject directionDriving(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/direction/driving", paramMap);
            log.info("Amap.directionDriving=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionDriving error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 骑行路径规划
     * see https://lbs.amap.com/api/webservice/guide/api/direction
     */
    public JSONObject directionBicycling(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/direction/bicycling", paramMap);
            log.info("Amap.directionBicycling=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionBicycling error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 公交路径规划
     * see https://lbs.amap.com/api/webservice/guide/api/direction
     */
    public JSONObject directionTransit(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/direction/transit/integrated", paramMap);
            log.info("Amap.directionTransit=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.directionTransit error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 静态地图
     * see https://lbs.amap.com/api/webservice/guide/api/staticmaps
     */
    public String staticmap(JSONObject paramMap) {
        return StrUtil.format("https://restapi.amap.com/v3/staticmap?{}", HttpUtil.toParams(paramMap));
    }

    /**
     * 天气查询
     * see https://lbs.amap.com/api/webservice/guide/api/weatherinfo
     */
    public JSONObject weatherInfo(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/weather/weatherInfo", paramMap);
            log.info("Amap.weatherInfo=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.weatherInfo error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * IP定位
     * see https://lbs.amap.com/api/webservice/guide/api/ipconfig
     */
    public JSONObject ip(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/ip", paramMap);
            log.info("Amap.ip=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.ip error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 坐标转换
     * see https://lbs.amap.com/api/webservice/guide/api/convert
     */
    public JSONObject coordinateConvert(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/assistant/coordinate/convert", paramMap);
            log.info("Amap.coordinateConvert=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.coordinateConvert error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }


    /**
     * 搜索POI
     * see https://lbs.amap.com/api/webservice/guide/api/search
     */
    public JSONObject poi(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v3/place/text", paramMap);
            log.info("Amap.poi=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.poi error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

    /**
     * 搜索POI 2.0
     * see https://lbs.amap.com/api/webservice/guide/api/newpoisearch
     */
    public JSONObject poiV2(JSONObject paramMap) {
        try {
            TimeInterval timeInterval = DateUtil.timer();
            String result = HttpUtil.get("https://restapi.amap.com/v5/place/text", paramMap);
            log.info("Amap.poiV2=>{}ms", timeInterval.interval());
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("Amap.poiV2 error", e);
            return new JSONObject().set("status", -1).set("msg", "接口调用异常");
        }
    }

}
