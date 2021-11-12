package com.nb6868.onex.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * 模板引擎工具
 * see {https://www.hutool.cn/docs/#/extra/%E6%A8%A1%E6%9D%BF%E5%BC%95%E6%93%8E/%E6%A8%A1%E6%9D%BF%E5%BC%95%E6%93%8E%E5%B0%81%E8%A3%85-TemplateUtil}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TemplateUtils {

    /**
     * 获取模板文字渲染后的内容
     * 注意需要引入模板引擎，比如freemarker velocity
     *
     * @param raw    模板原始内容
     * @param params 参数
     */
    public static String renderRaw(String raw, Map<String, Object> params) {
        if (StrUtil.isBlank(raw) || CollectionUtils.isEmpty(params)) {
            return raw;
        }
        // 自动根据用户引入的模板引擎库的jar来自动选择使用的引擎
        // TemplateConfig为模板引擎的选项，可选内容有字符编码、模板路径、模板加载方式等，默认通过模板字符串渲染
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setCharset(Charset.defaultCharset());
        Template template = TemplateUtil.createEngine(templateConfig).getTemplate(raw);
        return template.render(params);
    }

}
