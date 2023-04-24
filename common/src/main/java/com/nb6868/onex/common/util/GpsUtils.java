package com.nb6868.onex.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 地理位置工具
 * 坐标系转换https://github.com/xinconan/coordtransform
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class GpsUtils {

    private static final double PI = Math.PI;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double PK = 180 / PI;

    /**
     * 点偏移,从指定的原点出发，偏移输入角度后，向此方向延伸输入距离，返回此时的位置
     *
     * @param origin   原点
     * @param azimuth  偏移角度(0-359), 正北方：0/360 正东方：90 正南方：180 正西方：270
     * @param distance 延伸距离(单位米)
     * @return 返回位置的经度纬度
     */
    public static LngLat pointOffset(LngLat origin, int azimuth, double distance) {
        return new LngLat(origin.getLng() + distance * Math.sin(azimuth / PK) * 180 / (PI * A * Math.cos(origin.getLat() / PK)), origin.getLat() + distance * Math.cos(azimuth / PK) / (A / PK));
    }

    /**
     * 根据经纬度计算两点之间的距离
     *
     * @return 距离
     */
    public static long getDistance(LngLat pointA, LngLat pointB) {
        double t1 = Math.cos(pointA.getLat() / PK) * Math.cos(pointA.getLng() / PK) * Math.cos(pointB.getLat() / PK) * Math.cos(pointB.getLng() / PK);
        double t2 = Math.cos(pointA.getLat() / PK) * Math.sin(pointA.getLng() / PK) * Math.cos(pointB.getLat() / PK) * Math.sin(pointB.getLng() / PK);
        double t3 = Math.sin(pointA.getLat() / PK) * Math.sin(pointB.getLat() / PK);

        return (long) (6366000 * Math.acos(t1 + t2 + t3));
    }

    /**
     * 地球坐标系 WGS-84 to 火星坐标系 GCJ-02
     */
    public static LngLat calWGS84toGCJ02(LngLat point) {
        LngLat dev = calDev(point);
        return new LngLat(point.getLng() + dev.getLng(), point.getLat() + dev.getLat());
    }

    /**
     * 地球坐标系 WGS-84 to 百度坐标系 BD-09
     */
    public static LngLat calWGS84toBD09(LngLat point) {
        LngLat dev = calDev(point);
        return calGCJ02toBD09(new LngLat(point.getLng() + dev.getLng(), point.getLat() + dev.getLat()));
    }

    /**
     * 火星坐标系 GCJ-02 to 地球坐标系 WGS-84
     */
    public static LngLat calGCJ02toWGS84(LngLat point) {
        LngLat dev = calDev(point);
        double retLat = point.getLat() - dev.getLat();
        double retLon = point.getLng() - dev.getLng();
        dev = calDev(new LngLat(retLon, retLat));
        retLat = point.getLat() - dev.getLat();
        retLon = point.getLng() - dev.getLng();
        return new LngLat(retLon, retLat);
    }

    /**
     * 百度坐标系 BD-09 to 地球坐标系 WGS-84
     */
    public static LngLat calBD09toWGS84(LngLat point) {
        LngLat gcj = calBD09toGCJ02(point);
        return calGCJ02toWGS84(gcj);
    }

    /**
     * 火星坐标系 GCJ-02 to 百度坐标系 BD-09
     */
    public static LngLat calGCJ02toBD09(LngLat point) {
        double x = point.getLng(), y = point.getLat();
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        double retLat = z * Math.sin(theta) + 0.006;
        double retLon = z * Math.cos(theta) + 0.0065;
        return new LngLat(retLon, retLat);
    }

    /**
     * 百度坐标系 BD-09 to 火星坐标系 GCJ-02
     */
    public static LngLat calBD09toGCJ02(LngLat point) {
        double x = point.getLng() - 0.0065, y = point.getLat() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        double retLat = z * Math.sin(theta);
        double retLon = z * Math.cos(theta);
        return new LngLat(retLon, retLat);
    }

    /**
     * 判断坐标是否在国内
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param precision 是否精确判断范围
     * @return true 在国外，false 在国内
     */
    public static boolean isOutOfChina(double latitude, double longitude, boolean precision) {
        if (precision) {
            return CHINA_POLYGON.stream().noneMatch(point -> pointInPolygon(point, latitude, longitude));
        } else {
            if (longitude < 72.004 || longitude > 137.8347) {
                return true;
            }
            return latitude < 0.8293 || latitude > 55.8271;
        }
    }

    private static LngLat calDev(LngLat point) {
        if (isOutOfChina(point.getLat(), point.getLng(), false)) {
            return new LngLat(point.getLat(), point.getLat());
        }
        double dLat = calLat(point.getLng() - 105.0, point.getLat() - 35.0);
        double dLon = calLon(point.getLng() - 105.0, point.getLat() - 35.0);
        double radLat = point.getLat() / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        return new LngLat(dLat, dLon);
    }

    private static double calLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double calLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 检查坐标点是否在多边形区域内
     *
     * @param polygon   多边形
     * @param latitude  纬度
     * @param longitude 经度
     * @return true 在多边形区域内，false 在多边形区域外
     */
    private static boolean pointInPolygon(LngLat[] polygon, double latitude, double longitude) {
        int i, j = polygon.length - 1;
        boolean oddNodes = false;
        for (i = 0; i < polygon.length; i++) {
            if ((polygon[i].getLat() < latitude && polygon[j].getLat() >= latitude
                    || polygon[j].getLat() < latitude && polygon[i].getLat() >= latitude)
                    && (polygon[i].getLng() <= longitude || polygon[j].getLng() <= longitude)) {
                if (polygon[i].getLng()
                        + (latitude - polygon[i].getLat()) / (polygon[j].getLat() - polygon[i].getLat())
                        * (polygon[j].getLng() - polygon[i].getLng())
                        < longitude) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }

    /**
     * 经纬度点
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class LngLat {
        private double lng;
        private double lat;

        @Override
        public String toString() {
            return lng + "," + lat;
        }
    }

    //region 中国行政边界的WGS84坐标数据
    // 大陆
    private static final LngLat[] MAINLAND = new LngLat[]{
            new LngLat(27.32083, 88.91693),
            new LngLat(27.54243, 88.76464),
            new LngLat(28.00805, 88.83575),
            new LngLat(28.1168, 88.62435),
            new LngLat(27.86605, 88.14279),
            new LngLat(27.82305, 87.19275),
            new LngLat(28.11166, 86.69527),
            new LngLat(27.90888, 86.45137),
            new LngLat(28.15805, 86.19769),
            new LngLat(27.88625, 86.0054),
            new LngLat(28.27916, 85.72137),
            new LngLat(28.30666, 85.11095),
            new LngLat(28.59104, 85.19518),
            new LngLat(28.54444, 84.84665),
            new LngLat(28.73402, 84.48623),
            new LngLat(29.26097, 84.11651),
            new LngLat(29.18902, 83.5479),
            new LngLat(29.63166, 83.19109),
            new LngLat(30.06923, 82.17525),
            new LngLat(30.33444, 82.11123),
            new LngLat(30.385, 81.42623),
            new LngLat(30.01194, 81.23221),
            new LngLat(30.20435, 81.02536),
            new LngLat(30.57552, 80.207),
            new LngLat(30.73374, 80.25423),
            new LngLat(30.96583, 79.86304),
            new LngLat(30.95708, 79.55429),
            new LngLat(31.43729, 79.08082),
            new LngLat(31.30895, 78.76825),
            new LngLat(31.96847, 78.77075),
            new LngLat(32.24304, 78.47594),
            new LngLat(32.5561, 78.40595),
            new LngLat(32.63902, 78.74623),
            new LngLat(32.35083, 78.9711),
            new LngLat(32.75666, 79.52874),
            new LngLat(33.09944, 79.37511),
            new LngLat(33.42863, 78.93623),
            new LngLat(33.52041, 78.81387),
            new LngLat(34.06833, 78.73581),
            new LngLat(34.35001, 78.98535),
            new LngLat(34.6118, 78.33707),
            new LngLat(35.28069, 78.02305),
            new LngLat(35.49902, 78.0718),
            new LngLat(35.50133, 77.82393),
            new LngLat(35.6125, 76.89526),
            new LngLat(35.90665, 76.55304),
            new LngLat(35.81458, 76.18061),
            new LngLat(36.07082, 75.92887),
            new LngLat(36.23751, 76.04166),
            new LngLat(36.66343, 75.85984),
            new LngLat(36.73169, 75.45179),
            new LngLat(36.91156, 75.39902),
            new LngLat(36.99719, 75.14787),
            new LngLat(37.02782, 74.56543),
            new LngLat(37.17, 74.39089),
            new LngLat(37.23733, 74.91574),
            new LngLat(37.40659, 75.18748),
            new LngLat(37.65243, 74.9036),
            new LngLat(38.47256, 74.85442),
            new LngLat(38.67438, 74.35471),
            new LngLat(38.61271, 73.81401),
            new LngLat(38.88653, 73.70818),
            new LngLat(38.97256, 73.85235),
            new LngLat(39.23569, 73.62005),
            new LngLat(39.45483, 73.65569),
            new LngLat(39.59965, 73.95471),
            new LngLat(39.76896, 73.8429),
            new LngLat(40.04202, 73.99096),
            new LngLat(40.32792, 74.88089),
            new LngLat(40.51723, 74.8588),
            new LngLat(40.45042, 75.23394),
            new LngLat(40.64452, 75.58284),
            new LngLat(40.298, 75.70374),
            new LngLat(40.35324, 76.3344),
            new LngLat(41.01258, 76.87067),
            new LngLat(41.04079, 78.08083),
            new LngLat(41.39286, 78.39554),
            new LngLat(42.03954, 80.24513),
            new LngLat(42.19622, 80.23402),
            new LngLat(42.63245, 80.15804),
            new LngLat(42.81565, 80.25796),
            new LngLat(42.88545, 80.57226),
            new LngLat(43.02906, 80.38405),
            new LngLat(43.1683, 80.81526),
            new LngLat(44.11378, 80.36887),
            new LngLat(44.6358, 80.38499),
            new LngLat(44.73408, 80.51589),
            new LngLat(44.90282, 79.87106),
            new LngLat(45.3497, 81.67928),
            new LngLat(45.15748, 81.94803),
            new LngLat(45.13303, 82.56638),
            new LngLat(45.43581, 82.64624),
            new LngLat(45.5831, 82.32179),
            new LngLat(47.20061, 83.03443),
            new LngLat(46.97332, 83.93026),
            new LngLat(46.99361, 84.67804),
            new LngLat(46.8277, 84.80318),
            new LngLat(47.0591, 85.52257),
            new LngLat(47.26221, 85.70139),
            new LngLat(47.93721, 85.53707),
            new LngLat(48.39333, 85.76596),
            new LngLat(48.54277, 86.59791),
            new LngLat(49.1102, 86.87602),
            new LngLat(49.09262, 87.34821),
            new LngLat(49.17295, 87.8407),
            new LngLat(48.98304, 87.89291),
            new LngLat(48.88103, 87.7611),
            new LngLat(48.73499, 88.05942),
            new LngLat(48.56541, 87.99194),
            new LngLat(48.40582, 88.51679),
            new LngLat(48.21193, 88.61179),
            new LngLat(47.99374, 89.08514),
            new LngLat(47.88791, 90.07096),
            new LngLat(46.95221, 90.9136),
            new LngLat(46.57735, 91.07027),
            new LngLat(46.29694, 90.92151),
            new LngLat(46.01735, 91.02651),
            new LngLat(45.57972, 90.68193),
            new LngLat(45.25305, 90.89694),
            new LngLat(45.07729, 91.56088),
            new LngLat(44.95721, 93.5547),
            new LngLat(44.35499, 94.71735),
            new LngLat(44.29416, 95.41061),
            new LngLat(44.01937, 95.34109),
            new LngLat(43.99311, 95.53339),
            new LngLat(43.28388, 95.87901),
            new LngLat(42.73499, 96.38206),
            new LngLat(42.79583, 97.1654),
            new LngLat(42.57194, 99.51012),
            new LngLat(42.67707, 100.8425),
            new LngLat(42.50972, 101.8147),
            new LngLat(42.23333, 102.0772),
            new LngLat(41.88721, 103.4164),
            new LngLat(41.87721, 104.5267),
            new LngLat(41.67068, 104.5237),
            new LngLat(41.58666, 105.0065),
            new LngLat(42.46624, 107.4758),
            new LngLat(42.42999, 109.3107),
            new LngLat(42.64576, 110.1064),
            new LngLat(43.31694, 110.9897),
            new LngLat(43.69221, 111.9583),
            new LngLat(44.37527, 111.4214),
            new LngLat(45.04944, 111.873),
            new LngLat(45.08055, 112.4272),
            new LngLat(44.8461, 112.853),
            new LngLat(44.74527, 113.638),
            new LngLat(45.38943, 114.5453),
            new LngLat(45.4586, 115.7019),
            new LngLat(45.72193, 116.2104),
            new LngLat(46.29583, 116.5855),
            new LngLat(46.41888, 117.3755),
            new LngLat(46.57069, 117.425),
            new LngLat(46.53645, 117.8455),
            new LngLat(46.73638, 118.3147),
            new LngLat(46.59895, 119.7068),
            new LngLat(46.71513, 119.9315),
            new LngLat(46.90221, 119.9225),
            new LngLat(47.66499, 119.125),
            new LngLat(47.99475, 118.5393),
            new LngLat(48.01125, 117.8046),
            new LngLat(47.65741, 117.3827),
            new LngLat(47.88805, 116.8747),
            new LngLat(47.87819, 116.2624),
            new LngLat(47.69186, 115.9231),
            new LngLat(47.91749, 115.5944),
            new LngLat(48.14353, 115.5491),
            new LngLat(48.25249, 115.8358),
            new LngLat(48.52055, 115.8111),
            new LngLat(49.83047, 116.7114),
            new LngLat(49.52058, 117.8747),
            new LngLat(49.92263, 118.5746),
            new LngLat(50.09631, 119.321),
            new LngLat(50.33028, 119.36),
            new LngLat(50.39027, 119.1386),
            new LngLat(51.62083, 120.0641),
            new LngLat(52.115, 120.7767),
            new LngLat(52.34423, 120.6259),
            new LngLat(52.54267, 120.7122),
            new LngLat(52.58805, 120.0819),
            new LngLat(52.76819, 120.0314),
            new LngLat(53.26374, 120.8307),
            new LngLat(53.54361, 123.6147),
            new LngLat(53.18832, 124.4933),
            new LngLat(53.05027, 125.62),
            new LngLat(52.8752, 125.6573),
            new LngLat(52.75722, 126.0968),
            new LngLat(52.5761, 125.9943),
            new LngLat(52.12694, 126.555),
            new LngLat(51.99437, 126.4412),
            new LngLat(51.38138, 126.9139),
            new LngLat(51.26555, 126.8176),
            new LngLat(51.31923, 126.9689),
            new LngLat(51.05825, 126.9331),
            new LngLat(50.74138, 127.2919),
            new LngLat(50.31472, 127.334),
            new LngLat(50.20856, 127.5861),
            new LngLat(49.80588, 127.515),
            new LngLat(49.58665, 127.838),
            new LngLat(49.58443, 128.7119),
            new LngLat(49.34676, 129.1118),
            new LngLat(49.4158, 129.4902),
            new LngLat(48.86464, 130.2246),
            new LngLat(48.86041, 130.674),
            new LngLat(48.60576, 130.5236),
            new LngLat(48.3268, 130.824),
            new LngLat(48.10839, 130.6598),
            new LngLat(47.68721, 130.9922),
            new LngLat(47.71027, 132.5211),
            new LngLat(48.09888, 133.0827),
            new LngLat(48.06888, 133.4843),
            new LngLat(48.39112, 134.4153),
            new LngLat(48.26713, 134.7408),
            new LngLat(47.99207, 134.5576),
            new LngLat(47.70027, 134.7608),
            new LngLat(47.32333, 134.1825),
            new LngLat(46.64017, 133.9977),
            new LngLat(46.47888, 133.8472),
            new LngLat(46.25363, 133.9016),
            new LngLat(45.82347, 133.4761),
            new LngLat(45.62458, 133.4702),
            new LngLat(45.45083, 133.1491),
            new LngLat(45.05694, 133.0253),
            new LngLat(45.34582, 131.8684),
            new LngLat(44.97388, 131.4691),
            new LngLat(44.83649, 130.953),
            new LngLat(44.05193, 131.298),
            new LngLat(43.53624, 131.1912),
            new LngLat(43.38958, 131.3104),
            new LngLat(42.91645, 131.1285),
            new LngLat(42.74485, 130.4327),
            new LngLat(42.42186, 130.6044),
            new LngLat(42.71416, 130.2468),
            new LngLat(42.88794, 130.2514),
            new LngLat(43.00457, 129.9046),
            new LngLat(42.43582, 129.6955),
            new LngLat(42.44624, 129.3493),
            new LngLat(42.02736, 128.9269),
            new LngLat(42.00124, 128.0566),
            new LngLat(41.58284, 128.3002),
            new LngLat(41.38124, 128.1529),
            new LngLat(41.47249, 127.2708),
            new LngLat(41.79222, 126.9047),
            new LngLat(41.61176, 126.5661),
            new LngLat(40.89694, 126.0118),
            new LngLat(40.47037, 124.8851),
            new LngLat(40.09362, 124.3736),
            new LngLat(39.82777, 124.128),
            new LngLat(39.8143, 123.2422),
            new LngLat(39.67388, 123.2167),
            new LngLat(38.99638, 121.648),
            new LngLat(38.8611, 121.6982),
            new LngLat(38.71909, 121.1873),
            new LngLat(38.91221, 121.0887),
            new LngLat(39.09013, 121.6794),
            new LngLat(39.2186, 121.5994),
            new LngLat(39.35166, 121.7511),
            new LngLat(39.52847, 121.2283),
            new LngLat(39.62322, 121.533),
            new LngLat(39.81138, 121.4683),
            new LngLat(40.00305, 121.881),
            new LngLat(40.50562, 122.2987),
            new LngLat(40.73874, 122.0521),
            new LngLat(40.92194, 121.1775),
            new LngLat(40.1961, 120.4468),
            new LngLat(39.87242, 119.5264),
            new LngLat(39.15693, 118.9715),
            new LngLat(39.04083, 118.3273),
            new LngLat(39.19846, 117.889),
            new LngLat(38.67555, 117.5364),
            new LngLat(38.38666, 117.6722),
            new LngLat(38.16721, 118.0281),
            new LngLat(38.1529, 118.8378),
            new LngLat(37.87832, 119.0355),
            new LngLat(37.30054, 118.9566),
            new LngLat(37.14361, 119.2328),
            new LngLat(37.15138, 119.7672),
            new LngLat(37.35228, 119.8529),
            new LngLat(37.83499, 120.7371),
            new LngLat(37.42458, 121.58),
            new LngLat(37.55256, 122.1282),
            new LngLat(37.41833, 122.1814),
            new LngLat(37.39624, 122.5586),
            new LngLat(37.20999, 122.5972),
            new LngLat(37.02583, 122.4005),
            new LngLat(37.01978, 122.5392),
            new LngLat(36.89361, 122.5047),
            new LngLat(36.84298, 122.1923),
            new LngLat(37.00027, 121.9566),
            new LngLat(36.75889, 121.5944),
            new LngLat(36.61666, 120.7764),
            new LngLat(36.52638, 120.96),
            new LngLat(36.37582, 120.8753),
            new LngLat(36.42277, 120.7062),
            new LngLat(36.14075, 120.6956),
            new LngLat(36.0419, 120.3436),
            new LngLat(36.26345, 120.3078),
            new LngLat(36.19998, 120.0889),
            new LngLat(35.95943, 120.2378),
            new LngLat(35.57893, 119.6475),
            new LngLat(34.88499, 119.1761),
            new LngLat(34.31145, 120.2487),
            new LngLat(32.97499, 120.8858),
            new LngLat(32.63889, 120.8375),
            new LngLat(32.42958, 121.3348),
            new LngLat(32.11333, 121.4412),
            new LngLat(32.02166, 121.7066),
            new LngLat(31.67833, 121.8275),
            new LngLat(31.86639, 120.9444),
            new LngLat(32.09361, 120.6019),
            new LngLat(31.94555, 120.099),
            new LngLat(32.30638, 119.8267),
            new LngLat(32.26277, 119.6317),
            new LngLat(31.90388, 120.1364),
            new LngLat(31.98833, 120.7026),
            new LngLat(31.81944, 120.7196),
            new LngLat(31.30889, 121.6681),
            new LngLat(30.97986, 121.8828),
            new LngLat(30.85305, 121.8469),
            new LngLat(30.56889, 120.9915),
            new LngLat(30.33555, 120.8144),
            new LngLat(30.39298, 120.4586),
            new LngLat(30.19694, 120.15),
            new LngLat(30.31027, 120.5082),
            new LngLat(30.06465, 120.7916),
            new LngLat(30.30458, 121.2808),
            new LngLat(29.96305, 121.6778),
            new LngLat(29.88211, 122.1196),
            new LngLat(29.51167, 121.4483),
            new LngLat(29.58916, 121.9744),
            new LngLat(29.19527, 121.9336),
            new LngLat(29.18388, 121.8119),
            new LngLat(29.37236, 121.7969),
            new LngLat(29.19729, 121.7444),
            new LngLat(29.29111, 121.5611),
            new LngLat(29.1634, 121.4135),
            new LngLat(29.02194, 121.6914),
            new LngLat(28.9359, 121.4908),
            new LngLat(28.72798, 121.6113),
            new LngLat(28.84215, 121.1464),
            new LngLat(28.66993, 121.4844),
            new LngLat(28.34722, 121.6417),
            new LngLat(28.13889, 121.3419),
            new LngLat(28.38277, 121.1651),
            new LngLat(27.98222, 120.9353),
            new LngLat(28.07944, 120.5908),
            new LngLat(27.87229, 120.84),
            new LngLat(27.59319, 120.5812),
            new LngLat(27.45083, 120.6655),
            new LngLat(27.20777, 120.5075),
            new LngLat(27.28278, 120.1896),
            new LngLat(27.14764, 120.4211),
            new LngLat(26.89805, 120.0332),
            new LngLat(26.64465, 120.128),
            new LngLat(26.51778, 119.8603),
            new LngLat(26.78823, 120.0733),
            new LngLat(26.64888, 119.8668),
            new LngLat(26.79611, 119.7879),
            new LngLat(26.75625, 119.5503),
            new LngLat(26.44222, 119.8204),
            new LngLat(26.47388, 119.5775),
            new LngLat(26.33861, 119.658),
            new LngLat(26.36777, 119.9489),
            new LngLat(25.99694, 119.4253),
            new LngLat(26.14041, 119.0975),
            new LngLat(25.93788, 119.354),
            new LngLat(25.99069, 119.7058),
            new LngLat(25.67996, 119.5807),
            new LngLat(25.68222, 119.4522),
            new LngLat(25.35333, 119.6454),
            new LngLat(25.60649, 119.3149),
            new LngLat(25.42097, 119.1053),
            new LngLat(25.25319, 119.3526),
            new LngLat(25.17208, 119.2726),
            new LngLat(25.2426, 118.8749),
            new LngLat(24.97194, 118.9866),
            new LngLat(24.88291, 118.5729),
            new LngLat(24.75673, 118.7631),
            new LngLat(24.52861, 118.5953),
            new LngLat(24.53638, 118.2397),
            new LngLat(24.68194, 118.1688),
            new LngLat(24.44024, 118.0199),
            new LngLat(24.46019, 117.7947),
            new LngLat(24.25875, 118.1237),
            new LngLat(23.62437, 117.1957),
            new LngLat(23.65919, 116.9179),
            new LngLat(23.355, 116.7603),
            new LngLat(23.42024, 116.5322),
            new LngLat(23.23666, 116.7871),
            new LngLat(23.21083, 116.5139),
            new LngLat(22.93902, 116.4817),
            new LngLat(22.73916, 115.7978),
            new LngLat(22.88416, 115.6403),
            new LngLat(22.65889, 115.5367),
            new LngLat(22.80833, 115.1614),
            new LngLat(22.70277, 114.8889),
            new LngLat(22.53305, 114.8722),
            new LngLat(22.64027, 114.718),
            new LngLat(22.81402, 114.7782),
            new LngLat(22.69972, 114.5208),
            new LngLat(22.50423, 114.6136),
            new LngLat(22.55004, 114.2223),
            new LngLat(22.42993, 114.3885),
            new LngLat(22.26056, 114.2961),
            new LngLat(22.36736, 113.9056),
            new LngLat(22.50874, 114.0337),
            new LngLat(22.47444, 113.8608),
            new LngLat(22.83458, 113.606),
            new LngLat(23.05027, 113.5253),
            new LngLat(23.11724, 113.8219),
            new LngLat(23.05083, 113.4793),
            new LngLat(22.87986, 113.3629),
            new LngLat(22.54944, 113.5648),
            new LngLat(22.18701, 113.5527),
            new LngLat(22.56701, 113.1687),
            new LngLat(22.17965, 113.3868),
            new LngLat(22.04069, 113.2226),
            new LngLat(22.20485, 113.0848),
            new LngLat(21.8693, 112.94),
            new LngLat(21.96472, 112.824),
            new LngLat(21.70139, 112.2819),
            new LngLat(21.91611, 111.8921),
            new LngLat(21.75139, 111.9669),
            new LngLat(21.77819, 111.6762),
            new LngLat(21.61264, 111.7832),
            new LngLat(21.5268, 111.644),
            new LngLat(21.52528, 111.0285),
            new LngLat(21.21138, 110.5328),
            new LngLat(21.37322, 110.3944),
            new LngLat(20.84381, 110.1594),
            new LngLat(20.84083, 110.3755),
            new LngLat(20.64, 110.3239),
            new LngLat(20.48618, 110.5274),
            new LngLat(20.24611, 110.2789),
            new LngLat(20.2336, 109.9244),
            new LngLat(20.4318, 110.0069),
            new LngLat(20.92416, 109.6629),
            new LngLat(21.44694, 109.9411),
            new LngLat(21.50569, 109.6605),
            new LngLat(21.72333, 109.5733),
            new LngLat(21.49499, 109.5344),
            new LngLat(21.39666, 109.1428),
            new LngLat(21.58305, 109.1375),
            new LngLat(21.61611, 108.911),
            new LngLat(21.79889, 108.8702),
            new LngLat(21.59888, 108.7403),
            new LngLat(21.93562, 108.4692),
            new LngLat(21.59014, 108.5125),
            new LngLat(21.68999, 108.3336),
            new LngLat(21.51444, 108.2447),
            new LngLat(21.54241, 107.99),
            new LngLat(21.66694, 107.7831),
            new LngLat(21.60526, 107.3627),
            new LngLat(22.03083, 106.6933),
            new LngLat(22.45682, 106.5517),
            new LngLat(22.76389, 106.7875),
            new LngLat(22.86694, 106.7029),
            new LngLat(22.91253, 105.8771),
            new LngLat(23.32416, 105.3587),
            new LngLat(23.18027, 104.9075),
            new LngLat(22.81805, 104.7319),
            new LngLat(22.6875, 104.3747),
            new LngLat(22.79812, 104.1113),
            new LngLat(22.50387, 103.9687),
            new LngLat(22.78287, 103.6538),
            new LngLat(22.58436, 103.5224),
            new LngLat(22.79451, 103.3337),
            new LngLat(22.43652, 103.0304),
            new LngLat(22.77187, 102.4744),
            new LngLat(22.39629, 102.1407),
            new LngLat(22.49777, 101.7415),
            new LngLat(22.20916, 101.5744),
            new LngLat(21.83444, 101.7653),
            new LngLat(21.14451, 101.786),
            new LngLat(21.17687, 101.2919),
            new LngLat(21.57264, 101.1482),
            new LngLat(21.76903, 101.099),
            new LngLat(21.47694, 100.6397),
            new LngLat(21.43546, 100.2057),
            new LngLat(21.72555, 99.97763),
            new LngLat(22.05018, 99.95741),
            new LngLat(22.15592, 99.16785),
            new LngLat(22.93659, 99.56484),
            new LngLat(23.08204, 99.5113),
            new LngLat(23.18916, 98.92747),
            new LngLat(23.97076, 98.67991),
            new LngLat(24.16007, 98.89073),
            new LngLat(23.92999, 97.54762),
            new LngLat(24.26055, 97.7593),
            new LngLat(24.47666, 97.54305),
            new LngLat(24.73992, 97.55255),
            new LngLat(25.61527, 98.19109),
            new LngLat(25.56944, 98.36137),
            new LngLat(25.85597, 98.7104),
            new LngLat(26.12527, 98.56944),
            new LngLat(26.18472, 98.73109),
            new LngLat(26.79166, 98.77777),
            new LngLat(27.52972, 98.69699),
            new LngLat(27.6725, 98.45888),
            new LngLat(27.54014, 98.31992),
            new LngLat(28.14889, 98.14499),
            new LngLat(28.54652, 97.55887),
            new LngLat(28.22277, 97.34888),
            new LngLat(28.46749, 96.65387),
            new LngLat(28.35111, 96.40193),
            new LngLat(28.525, 96.34027),
            new LngLat(28.79569, 96.61373),
            new LngLat(29.05666, 96.47083),
            new LngLat(28.90138, 96.17532),
            new LngLat(29.05972, 96.14888),
            new LngLat(29.25757, 96.39172),
            new LngLat(29.46444, 96.08315),
            new LngLat(29.03527, 95.38777),
            new LngLat(29.33346, 94.64751),
            new LngLat(29.07348, 94.23456),
            new LngLat(28.6692, 93.96172),
            new LngLat(28.61876, 93.35194),
            new LngLat(28.3193, 93.22205),
            new LngLat(28.1419, 92.71044),
            new LngLat(27.86194, 92.54498),
            new LngLat(27.76472, 91.65776),
            new LngLat(27.945, 91.66277),
            new LngLat(28.08111, 91.30138),
            new LngLat(27.96999, 91.08693),
            new LngLat(28.07958, 90.3765),
            new LngLat(28.24257, 90.38898),
            new LngLat(28.32369, 89.99819),
            new LngLat(28.05777, 89.48749),
            new LngLat(27.32083, 88.91693)
    };

    // 台湾岛
    private static final LngLat[] TAIWAN = new LngLat[]{
            new LngLat(25.13474, 121.4441),
            new LngLat(25.28361, 121.5632),
            new LngLat(25.00722, 122.0004),
            new LngLat(24.85028, 121.8182),
            new LngLat(24.47638, 121.8397),
            new LngLat(23.0875, 121.3556),
            new LngLat(21.92791, 120.7196),
            new LngLat(22.31277, 120.6103),
            new LngLat(22.54044, 120.3071),
            new LngLat(23.04437, 120.0539),
            new LngLat(23.61708, 120.1112),
            new LngLat(25.00166, 121.0017),
            new LngLat(25.13474, 121.4441)
    };

    // 海南岛
    private static final LngLat[] HAINAN = new LngLat[]{
            new LngLat(19.52888, 110.855),
            new LngLat(19.16761, 110.4832),
            new LngLat(18.80083, 110.5255),
            new LngLat(18.3852, 110.0503),
            new LngLat(18.39152, 109.7594),
            new LngLat(18.19777, 109.7036),
            new LngLat(18.50562, 108.6871),
            new LngLat(19.28028, 108.6283),
            new LngLat(19.76, 109.2939),
            new LngLat(19.7236, 109.1653),
            new LngLat(19.89972, 109.2572),
            new LngLat(19.82861, 109.4658),
            new LngLat(19.99389, 109.6108),
            new LngLat(20.13361, 110.6655),
            new LngLat(19.97861, 110.9425),
            new LngLat(19.63829, 111.0215),
            new LngLat(19.52888, 110.855)
    };

    // 崇明岛
    private static final LngLat[] CHONGMING = new LngLat[]{
            new LngLat(31.80054, 121.2039),
            new LngLat(31.49972, 121.8736),
            new LngLat(31.53111, 121.5464),
            new LngLat(31.80054, 121.2039)
    };
    //endregion

    /**
     * 中国行政边界的WGS84坐标数据，
     * 光线投射算法 (Ray casting algorithm) 获得，
     * 沿海、国界周边地区可能会有误差，更高精度需要调整坐标点
     */
    private static final List<LngLat[]> CHINA_POLYGON = new ArrayList<>();

    static {
        CHINA_POLYGON.add(MAINLAND);
        CHINA_POLYGON.add(TAIWAN);
        CHINA_POLYGON.add(HAINAN);
        CHINA_POLYGON.add(CHONGMING);
    }
}
