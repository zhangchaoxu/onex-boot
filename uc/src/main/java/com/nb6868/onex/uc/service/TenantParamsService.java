package com.nb6868.onex.uc.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
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
    public String getContent(String tenantCode, String code) {
        return query()
                .select("content")
                .eq("tenant_code", tenantCode)
                .eq("code", code)
                .last(Const.LIMIT_ONE)
                .oneOpt()
                .map(TenantParamsEntity::getContent)
                .orElse(null);
    }

    /**
     * 根据参数编码，获取value的Object对象
     *
     * @param code  参数编码
     * @param clazz Object对象
     */
    public <T> T getContentObject(String tenantCode, String code, Class<T> clazz) {
        return JacksonUtils.jsonToPojo(getContent(tenantCode, code), clazz);
    }

}
