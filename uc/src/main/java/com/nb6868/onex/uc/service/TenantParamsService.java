package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.uc.dao.TenantParamsDao;
import com.nb6868.onex.uc.entity.TenantParamsEntity;
import org.springframework.stereotype.Service;

/**
 * 租户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantParamsService extends EntityService<TenantParamsDao, TenantParamsEntity> {

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public JSONObject getContent(String tenantCode, String code) {
        return getContent(tenantCode, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public JSONObject getContent(String tenantCode, String code, String contentJsonKey, String contentJsonValue) {
        return query()
                .select("content")
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .eq(StrUtil.isNotBlank(code), "code", code)
                .eq(StrUtil.isNotBlank(contentJsonKey) && StrUtil.isNotBlank(contentJsonValue), "content->'$." + contentJsonKey + "'", contentJsonValue)
                .last(Const.LIMIT_ONE)
                .oneOpt()
                .map(TenantParamsEntity::getContent)
                .orElse(null);
    }

}