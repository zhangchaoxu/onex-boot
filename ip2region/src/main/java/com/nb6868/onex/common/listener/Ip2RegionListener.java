package com.nb6868.onex.common.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.util.IpRegionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;

/**
 * 初初始化ip工具类
 * 使用方法，在application中
 * // 添加 日志监听器，使 log4j2-spring.xml 可以间接读取到配置文件的属性
 * application.addListeners(new Ip2RegionListener());
 *
 * @author 1024创新实验室: zhuoda
 * @author Charles zhangchaoxu@gmail.com
 */
@Order(value = LoggingApplicationListener.DEFAULT_ORDER)
@Slf4j
public class Ip2RegionListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    // 默认文件名称
    private static final String IP_FILE_NAME = "ip2region.xdb";

    @Value("${onex.log.ip2region.enable:false}")
    private boolean logIp2Region;
    @Value("${onex.log.ip2region.path}")
    private String logIp2RegionFilePath;

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEvent) {
        log.info("onex.log.ip2region.enable={}", logIp2Region);
        if (logIp2Region) {
            String ipFilePath = StrUtil.blankToDefault(logIp2RegionFilePath, new ClassPathResource(IP_FILE_NAME).getPath());
            if (FileUtil.exist(ipFilePath)) {
                // 初始化
                TimeInterval timer = DateUtil.timer();
                IpRegionUtil.init(ipFilePath);
                log.info("ip2region init:{}", timer.intervalPretty());
            } else {
                log.error("ip2region.xdb文件不存在{}", ipFilePath);
            }
        }
    }

}