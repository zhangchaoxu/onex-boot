package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.ParamsDao;
import com.nb6868.onex.uc.dto.ParamsDTO;
import com.nb6868.onex.uc.entity.ParamsEntity;
import org.springframework.stereotype.Service;

/**
 * 租户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ParamsService extends DtoService<ParamsDao, ParamsEntity, ParamsDTO> {

    /**
     * 获得用户参数
     */
    public String getSystemContent(String code) {
        return getContent(UcConst.ParamsTypeEnum.SYSTEM.value(), null, null, code, null, null);
    }

    /**
     * 获得用户参数
     */
    public String getUserContent(Long userId, String code) {
        return getContent(UcConst.ParamsTypeEnum.USER.value(), null, userId, code, null, null);
    }

    /**
     * 获得租户参数
     */
    public String getTenantContent(String tenantCode, String code) {
        return getContent(UcConst.ParamsTypeEnum.TENANT.value(), tenantCode, null, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public String getContent(Integer type, String tenantCode, Long userId, String code) {
        return getContent(type, tenantCode, userId, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public String getContent(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue) {
        return query().select("content")
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .eq(StrUtil.isNotBlank(code), "code", code)
                .eq(null != type, "type", type)
                .eq(null != userId, "user_id", userId)
                .eq(StrUtil.isAllNotBlank(contentJsonKey, contentJsonValue), "content->'$." + contentJsonKey + "'", contentJsonValue)
                .last(Const.LIMIT_ONE)
                .oneOpt()
                .map(ParamsEntity::getContent)
                .orElse(null);
    }

    /**
     * 获得用户参数
     */
    public <T> T getSystemContentObject(String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.SYSTEM.value(), null, null, code, null, null, clazz, defObj);
    }

    /**
     * 获得用户参数
     */
    public <T> T getUserContentObject(Long userId, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.USER.value(), null, userId, code, null, null, clazz, defObj);
    }

    /**
     * 获得租户参数
     */
    public <T> T getTenantContentObject(String tenantCode, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.TENANT.value(), tenantCode, null, code, null, null, clazz, defObj);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, Class<T> clazz, T defObj) {
        return getContentObject(type, tenantCode, userId, code, null, null, clazz, defObj);
    }

    public <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue, Class<T> clazz, T defObj) {
        String content = getContent(type, tenantCode, userId, code, contentJsonKey, contentJsonValue);
        return JacksonUtils.jsonToPojo(content, clazz, defObj);
    }

}
