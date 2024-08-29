package com.nb6868.onex.common.util;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

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

    public static final String BASE_URL = "https://restapi.amap.com";
    // 错误码,异常
    public static final String ERROR_CODE_EXCEPTION = "50500";
    // 错误码,请求参数问题
    public static final String ERROR_CODE_BAD_REQUEST = "50400";

    /**
     * 公共基础调用方法
     */
    public static ApiResult<JSONObject> baseCallApi(String url, JSONObject paramMap) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.isBlank(url) || ObjUtil.isEmpty(paramMap)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        try {
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("infocode", 0) == 10000)
                        .setCode(resultJson.getStr("infocode"))
                        .setMsg(resultJson.getStr("info"))
                        .setData(resultJson);
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 地理编码,数据在geocodes列表
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static ApiResult<JSONObject> geocodeGeo(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/geocode/geo", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 逆地理编码，数据在regeocode列表
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static ApiResult<JSONObject> geocodeRegeo(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/geocode/regeo", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 距离测量，数据在route列表，resultJson.getBeanList("route", JSONObject.class)
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> distance(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/distance", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 步行路径规划
     * resultJson.getBeanList("route", JSONObject.class)
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionWalking(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/walking", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 驾车路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionDriving(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/driving", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 骑行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionBicycling(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/bicycling", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 公交路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionTransitIntegrated(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/direction/transit/integrated", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
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
    public static ApiResult<JSONObject> weatherWeatherInfo(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/weather/weatherInfo", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * IP定位
     * <a href="https://lbs.amap.com/api/webservice/guide/api/ipconfig">...</a>
     */
    public static ApiResult<JSONObject> ip(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/ip", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 高级IP定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/ip">...</a>
     */
    public static ApiResult<JSONObject> ipLocation(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v5/ip/location", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 智能硬件定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/hardware-location">...</a>
     */
    public static ApiResult<JSONObject> position(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm("https://apilocate.amap.com/position", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 坐标转换
     * <a href="https://lbs.amap.com/api/webservice/guide/api/convert">...</a>
     */
    public static ApiResult<JSONObject> assistantCoordinateConvert(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/assistant/coordinate/convert", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 行政区域查询
     * <a href="https://lbs.amap.com/api/webservice/guide/api/district">...</a>
     */
    public static ApiResult<JSONObject> configDistrict(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/config/district", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 输入提示
     * <a href="https://lbs.amap.com/api/webservice/guide/api/inputtips">...</a>
     */
    public static ApiResult<JSONObject> assistantInputtips(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/assistant/inputtips", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 搜索POI
     * <a href="https://lbs.amap.com/api/webservice/guide/api/search">...</a>
     */
    public static ApiResult<JSONObject> placeText(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v3/place/text", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

    /**
     * 搜索POI 2.0
     * <a href="https://lbs.amap.com/api/webservice/guide/api/newpoisearch">...</a>
     */
    public static ApiResult<JSONObject> placeTextV2(JSONObject paramMap) {
        String url = HttpUtil.urlWithForm(BASE_URL + "/v5/place/text", paramMap, Charset.defaultCharset(), true);
        return baseCallApi(url, paramMap);
    }

}
