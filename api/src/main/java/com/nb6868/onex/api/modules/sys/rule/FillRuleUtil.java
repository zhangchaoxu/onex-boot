package com.nb6868.onex.api.modules.sys.rule;

import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;

public class FillRuleUtil {

    /**
     * @param ruleCode ruleCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object executeRule(String ruleCode, JsonObject formData) {
        if (!StrUtil.isEmpty(ruleCode)) {
            /*try {
                // 获取 Service
                ServiceImpl impl = (ServiceImpl) SpringContextUtils.getBean("sysFillRuleServiceImpl");
                // 根据 ruleCode 查询出实体
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("code", ruleCode);
                JsonObject entity = JSON.parseObject(JSON.toJSONString(impl.getOne(queryWrapper)));
                // 获取必要的参数
                String ruleClass = entity.getString("ruleClass");
                JsonObject params = entity.getJSONObject("ruleParams");
                if (params == null) {
                    params = new JsonObject();
                }
                if (formData == null) {
                    formData = new JsonObject();
                }
                // 通过反射执行配置的类里的方法
                FillRuleHandler ruleHandler = (FillRuleHandler) Class.forName(ruleClass).newInstance();
                return ruleHandler.execute(params, formData);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return null;
        }
        return null;
    }


}
