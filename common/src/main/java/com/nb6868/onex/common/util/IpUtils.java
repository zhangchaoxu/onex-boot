package com.nb6868.onex.common.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * IP工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class IpUtils {

    // 太平洋在线地址库
    public static String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

    /**
     * 根据ip获取详细地址
     */
    public static String getCity(String ip) {
        String api = String.format(IP_URL, ip);
        try {
            // 这里会因为接口报错导致程序报错
            JSONObject object = JSONUtil.parseObj(HttpUtil.get(api));
            return object.getStr("addr", "未知");
        } catch (Exception e) {
            return "异常";
        }
    }

}
