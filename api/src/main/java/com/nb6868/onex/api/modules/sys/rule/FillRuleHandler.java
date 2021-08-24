package com.nb6868.onex.api.modules.sys.rule;

import com.google.gson.JsonObject;

/**
 * 填值规则接口
 * <p>
 * 如需使用填值规则功能，规则实现类必须实现此接口
 *
 * @author Charles
 */
public interface FillRuleHandler {

    /**
     * @param params   页面配置固定参数
     * @param formData 动态表单参数
     */
    public Object execute(JsonObject params, JsonObject formData);

}
