package com.nb6868.onex.common.util;

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

    /**
     * 公共基础调用方法
     *
     * @param url 请求连接
     * @param paramMap 请求参数,会拼接到url中
     */
    public static ApiResult<JSONObject> baseCallApiGet(String url, JSONObject paramMap) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.isBlank(url)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS, "参数不能为空");
        }
        // 将参数拼接到url上
        url = HttpUtil.urlWithForm(url, paramMap, Charset.defaultCharset(), true);
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
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 地理编码,数据在geocodes列表
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static ApiResult<JSONObject> geocodeGeo(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/geocode/geo", paramMap);
    }

    /**
     * 逆地理编码，数据在regeocode列表
     * <a href="https://lbs.amap.com/api/webservice/guide/api/georegeo">...</a>
     */
    public static ApiResult<JSONObject> geocodeRegeo(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/geocode/regeo", paramMap);
    }

    /**
     * 距离测量，数据在route列表，resultJson.getBeanList("route", JSONObject.class)
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> distance(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/distance", paramMap);
    }

    /**
     * 步行路径规划
     * resultJson.getBeanList("route", JSONObject.class)
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionWalking(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/direction/walking", paramMap);
    }

    /**
     * 驾车路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionDriving(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/direction/driving", paramMap);
    }

    /**
     * 骑行路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionBicycling(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/direction/bicycling", paramMap);
    }

    /**
     * 公交路径规划
     * <a href="https://lbs.amap.com/api/webservice/guide/api/direction">...</a>
     */
    public static ApiResult<JSONObject> directionTransitIntegrated(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/direction/transit/integrated", paramMap);
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
        return baseCallApiGet(BASE_URL + "/v3/weather/weatherInfo", paramMap);
    }

    /**
     * IP定位
     * <a href="https://lbs.amap.com/api/webservice/guide/api/ipconfig">...</a>
     */
    public static ApiResult<JSONObject> ip(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/ip", paramMap);
    }

    /**
     * 高级IP定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/ip">...</a>
     */
    public static ApiResult<JSONObject> ipLocation(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v5/ip/location", paramMap);
    }

    /**
     * 智能硬件定位-属于高级服务接口
     * <a href="https://lbs.amap.com/api/webservice/guide/api-advanced/hardware-location">...</a>
     */
    public static ApiResult<JSONObject> position(JSONObject paramMap) {
        return baseCallApiGet("https://apilocate.amap.com/position", paramMap);
    }

    /**
     * 坐标转换
     * <a href="https://lbs.amap.com/api/webservice/guide/api/convert">...</a>
     */
    public static ApiResult<JSONObject> assistantCoordinateConvert(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/assistant/coordinate/convert", paramMap);
    }

    /**
     * 行政区域查询
     * <a href="https://lbs.amap.com/api/webservice/guide/api/district">...</a>
     */
    public static ApiResult<JSONObject> configDistrict(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/config/district", paramMap);
    }

    /**
     * 输入提示
     * <a href="https://lbs.amap.com/api/webservice/guide/api/inputtips">...</a>
     */
    public static ApiResult<JSONObject> assistantInputtips(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/assistant/inputtips", paramMap);
    }

    /**
     * 搜索POI
     * <a href="https://lbs.amap.com/api/webservice/guide/api/search">...</a>
     */
    public static ApiResult<JSONObject> placeText(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v3/place/text", paramMap);
    }

    /**
     * 搜索POI 2.0
     * <a href="https://lbs.amap.com/api/webservice/guide/api/newpoisearch">...</a>
     */
    public static ApiResult<JSONObject> placeTextV2(JSONObject paramMap) {
        return baseCallApiGet(BASE_URL + "/v5/place/text", paramMap);
    }

}
