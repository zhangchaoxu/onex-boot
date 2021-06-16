package com.nb6868.onexboot.api.common.util;

import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 模板引擎
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TemplateUtils {

    /**
     * 获取Freemarker渲染后的内容
     *
     * @param raw    模板原始内容
     * @param params 参数
     */
    public static String getTemplateContent(String templateName, String raw, Map<String, Object> params) {
        if (StrUtil.isBlank(raw) || CollectionUtils.isEmpty(params)) {
            return raw;
        }
        // 模板
        try {
            // 配置忽略未定义变量
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setClassicCompatible(true);
            Template template = new Template(templateName, new StringReader(raw), configuration, StandardCharsets.UTF_8.name());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return raw;
        }
    }

}
