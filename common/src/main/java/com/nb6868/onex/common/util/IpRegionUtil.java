package com.nb6868.onex.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * IP工具类
 * see <a href="https://github.com/lionsoul2014/ip2region/tree/master/binding/java">...</a>
 *
 * @author 1024创新实验室-主任:卓大
 */
@Slf4j
public class IpRegionUtil {

    private static Ip2Region ip2Region;

    /**
     * 初始化数据
     */
    public static boolean init(String v4File, String v6File) {
        try {
            InputStream v4Stream = ResourceUtil.getStreamSafe(v4File);
            InputStream v6Stream = ResourceUtil.getStreamSafe(v6File);
            ConfigBuilder v4ConfigBuilder = Config.custom()
                    .setCachePolicy(Config.VIndexCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    .setSearchers(15);                       // 设置初始化的查询器数量
            // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
            if (null == v4Stream) {
                if (FileUtil.exist(v4File)) {
                    v4ConfigBuilder.setXdbPath(v4File);
                } else {
                    return false;
                }
            } else {
                v4ConfigBuilder.setXdbInputStream(v4Stream);
            }

            ConfigBuilder v6ConfigBuilder = Config.custom()
                    .setCachePolicy(Config.VIndexCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    .setSearchers(15);                       // 设置初始化的查询器数量
            if (null == v6Stream) {
                if (FileUtil.exist(v6File)) {
                    v6ConfigBuilder.setXdbPath(v6File);
                } else {
                    return false;
                }
            } else {
                v6ConfigBuilder.setXdbInputStream(v6Stream);
            }
            // 3，通过上述配置创建 Ip2Region 查询服务
            ip2Region = Ip2Region.create(v4ConfigBuilder.asV4(), v6ConfigBuilder.asV6());
            return true;
        } catch (Throwable e) {
            log.error("初始化ip2region.xdb文件失败,报错信息:[{}]", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 自定义解析ip地址
     *
     * @param ipStr ipStr
     * @return 返回结果例 国家|区域|省份|城市|ISP，缺省的地域信息默认是0
     */
    public static List<String> getRegionList(String ipStr) {
        List<String> regionList = new ArrayList<>();
        try {
            if (StrUtil.isEmpty(ipStr)) {
                return regionList;
            }
            ipStr = ipStr.trim();
            String region = ip2Region.search(ipStr);
            String[] split = region.split("\\|");
            regionList.addAll(Arrays.asList(split));
        } catch (Exception e) {
            log.error("解析ip地址出错", e);
        } catch (Throwable throwable) {
            log.error("请引入org.lionsoul.ip2region依赖", throwable);
        }
        return regionList;
    }

    /**
     * 自定义解析ip地址
     *
     * @param ipStr ipStr
     * @return 返回结果例 国家|区域|省份|城市|ISP，缺省的地域信息默认是0
     */
    public static String getRegion(String ipStr) {
        if (StrUtil.isBlank(ipStr)) {
            return null;
        }
        try {
            return ip2Region.search(ipStr.trim());
        } catch (Exception e) {
            log.error("解析ip地址出错", e);
            return null;
        } catch (Throwable throwable) {
            log.error("请引入org.lionsoul.ip2region依赖", throwable);
            return null;
        }
    }

    /**
     * 获取本机第一个ip
     */
    public static String getLocalFirstIp() {
        List<String> list = getLocalIp();
        return !list.isEmpty() ? list.get(0) : null;
    }

    /**
     * 获取本机ip
     */
    public static List<String> getLocalIp() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 排除回环地址和IPv6地址
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().contains(":")) {
                        ipList.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            log.error("", e);
        }
        return ipList;
    }

}
