package com.nb6868.onexboot.common.util;

/**
 * 地理位置工具
 * 坐标系转换https://github.com/xinconan/coordtransform
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class GeoUtils {

    private final static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private final static double PI = 3.1415926535897932384626;
    private final static double a = 6378245.0;
    private final static double ee = 0.00669342162296594323;

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon
     * @param bd_lat
     * @return Double[lon, lat]
     */
    public static Double[] bd09ToGcj02(Double bd_lon, Double bd_lat) {
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        return new Double[]{z * Math.cos(theta), z * Math.sin(theta)};
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param gcj_lon
     * @param gcj_lat
     * @return Double[lon, lat]
     */
    public static Double[] gcj02ToBd09(Double gcj_lon, Double gcj_lat) {
        double z = Math.sqrt(gcj_lon * gcj_lon + gcj_lat * gcj_lat) + 0.00002 * Math.sin(gcj_lat * x_PI);
        double theta = Math.atan2(gcj_lat, gcj_lon) + 0.000003 * Math.cos(gcj_lon * x_PI);
        return new Double[]{z * Math.cos(theta) + 0.0065, z * Math.sin(theta) + 0.006};
    }

    /**
     * WGS84转GCJ02
     *
     * @param wgs_lon
     * @param wgs_lat
     * @return Double[lon, lat]
     */
    public static Double[] wgs84ToGcj02(Double wgs_lon, Double wgs_lat) {
        if (outOfChina(wgs_lon, wgs_lat)) {
            return new Double[]{wgs_lon, wgs_lat};
        }
        double dlat = transformLat(wgs_lon - 105.0, wgs_lat - 35.0);
        double dlng = transformLng(wgs_lon - 105.0, wgs_lat - 35.0);
        double radlat = wgs_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        return new Double[]{wgs_lon + dlng,wgs_lat + dlat};
    }

    /**
     * GCJ02转WGS84
     *
     * @param gcj_lon
     * @param gcj_lat
     * @return Double[lon, lat]
     */
    public static Double[] gcj02ToWGS84(Double gcj_lon, Double gcj_lat) {
        if (outOfChina(gcj_lon, gcj_lat)) {
            return new Double[]{gcj_lon, gcj_lat};
        }
        double dlat = transformLat(gcj_lon - 105.0, gcj_lat - 35.0);
        double dlng = transformLng(gcj_lon - 105.0, gcj_lat - 35.0);
        double radlat = gcj_lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = gcj_lat + dlat;
        double mglng = gcj_lon + dlng;
        return new Double[]{gcj_lon * 2 - mglng, gcj_lat * 2 - mglat};
    }

    private static Double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static Double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 判断是否在国内，不在国内则不做偏移
     */
    private static boolean outOfChina(Double lng, Double lat) {
        return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271));
    }

}
