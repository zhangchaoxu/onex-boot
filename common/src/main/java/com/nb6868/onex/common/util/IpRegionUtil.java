package com.nb6868.onex.common.util;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

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

    private static Searcher IP_SEARCHER;

    /**
     * 初始化数据
     */
    public static boolean initFromFile(String filePath, String version) {
        try {
            Searcher.verifyFromFile(filePath);
        } catch (Exception e) {
            // 适用性验证失败！！！
            // 当前查询客户端实现不适用于 dbPath 指定的 xdb 文件的查询.
            // 应该停止启动服务，使用合适的 xdb 文件或者升级到适合 dbPath 的 Searcher 实现。
            log.error("初始化ip2region.xdb文件失败,当前查询客户端实现不适用于 dbPath 指定的 xdb 文件的查询.");
            return false;
        }
        try {
            // 缓存整个 xdb 数据,从dbPath加载整个 xdb 到内存, 使用 LongByteArray 来存储，避免 xdb 文件过大的时候 int 类型的溢出
            LongByteArray cBuff = Searcher.loadContentFromFile(filePath);
            // 使用上述的 cBuff 创建一个完全基于内存的查询对象
            IP_SEARCHER = Searcher.newWithBuffer(Version.fromName(version), cBuff);
            return true;
        } catch (Throwable e) {
            log.error("初始化ip2region.xdb文件失败,报错信息:[{}]", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 初始化数据
     */
    public static boolean initFromResource(String resourcePath, String version) {
        try {
            // 从classpath的resource中读取stream=>byte[]
            byte[] cBuff = ResourceUtil.readBytes(resourcePath);
            // 使用上述的cBuff创建一个完全基于内存的查询对象
            IP_SEARCHER = Searcher.newWithBuffer(Version.fromName(version), new LongByteArray(cBuff));
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
            String region = IP_SEARCHER.search(ipStr);
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
            return IP_SEARCHER.search(ipStr.trim());
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
