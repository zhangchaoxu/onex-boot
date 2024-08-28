package com.nb6868.onex.common.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 高德 Web服务 API
 * 高德有应该隐藏参数s=rsv3,可以将webApi的接口当前端接口用,避免USERKEY_PLAT_NOMATCH的问题
 * <a href="https://lbs.amap.com/api/webservice/gettingstarted">文档</a>
 * <a href="https://lbs.amap.com/api/webservice/guide/tools/info">错误码</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AmapApiUtils {

    private static final String BASE_URL = "https://restapi.amap.com";

    /**
     * 地理编码
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static ApiResult<?> geocodeGeo(JSONObject paramMap) {
        ApiResult<List<JSONObject>> apiResult = new ApiResult<>();
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/geocode/geo", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("infocode", 0) == 10000)
                        .setCode(resultJson.getStr("infocode"))
                        .setMsg(resultJson.getStr("info"))
                        .setData(resultJson.getBeanList("geocodes", JSONObject.class));
            });
        } catch (Exception e) {
            apiResult.error("50000", "amap.v3/geocode/geo error:" + e.getMessage());
        }
        return apiResult;
    }

    /**
     * 逆地理编码
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> geocodeRegeo(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/geocode/regeo", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("regeocode", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/geocode/regeo error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 距离测量
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> distance(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/distance", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("route", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/distance error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 步行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> directionWalking(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/walking", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("route", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/direction/walking error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 驾车路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> directionDriving(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/driving", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("route", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/direction/driving error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 骑行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> directionBicycling(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/bicycling", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("route", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/direction/bicycling error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 公交路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> directionTransitIntegrated(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/transit/integrated", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("route", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/direction/transit/integrated error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 静态地图
     * <a href="https://lbs.amap.com/api/webservice/guide/api/staticmaps">...</a>
     */
    public static String staticmap(JSONObject paramMap) {
        return HttpUtil.urlWithForm(BASE_URL + "/v3/staticmap", paramMap, Charset.defaultCharset(), true);
    }

    /**
     * 天气查询
     * 结果中解析lives(实况天气)和forecast(预报天气)
     * <a href="https://lbs.amap.com/api/webservice/guide/api/weatherinfo">...</a>
     */
    public static Triple<Boolean, String, JSONObject> weatherWeatherInfo(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/weather/weatherInfo", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/weather/weatherInfo error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * IP定位
     * <a href="https://lbs.amap.com/api/webservice/guide/api/ipconfig">...</a>
     */
    public static Triple<Boolean, String, JSONObject> ip(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/ip", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/ip error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 高级IP定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/ip">...</a>
     */
    public static Triple<Boolean, String, JSONObject> ipLocation(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v5/ip/location", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v5/ip/location error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 智能硬件定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/hardware-location">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> position(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm("https://apilocate.amap.com/position", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("result", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.position error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 坐标转换
     * <a href="https://lbs.amap.com/api/webservice/guide/api/convert">...</a>
     */
    public static Triple<Boolean, String, String> assistantCoordinateConvert(JSONObject paramMap) {
        MutableTriple<Boolean, String, String> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/assistant/coordinate/convert", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getStr("locations"));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/assistant/coordinate/convert error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 行政区域查询
     * <a href="https://lbs.amap.com/api/webservice/guide/api/district">...</a>
     */
    public static Triple<Boolean, String, JSONObject> configDistrict(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/config/district", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/config/district error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 输入提示
     * <a href="https://lbs.amap.com/api/webservice/guide/api/inputtips">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> assistantInputtips(JSONObject paramMap) {
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/assistant/inputtips", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson.getBeanList("geocodes", JSONObject.class));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/assistant/inputtips error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 搜索POI
     * <a href="https://lbs.amap.com/api/webservice/guide/api/search">...</a>
     */
    public static Triple<Boolean, String, JSONObject> placeText(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v3/place/text", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v3/place/text error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 搜索POI 2.0
     * <a href="https://lbs.amap.com/api/webservice/guide/api/newpoisearch">...</a>
     */
    public static Triple<Boolean, String, JSONObject> placeTextV2(JSONObject paramMap) {
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/v5/place/text", paramMap, Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("status") == 1);
                triple.setMiddle(resultJson.getInt("infocode", 10000) + ":" + resultJson.getStr("info"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("amap.v5/place/text error:" + e.getMessage());
        }
        return triple;
    }

}
