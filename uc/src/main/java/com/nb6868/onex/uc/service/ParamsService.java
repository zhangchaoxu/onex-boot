package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.params.BaseParamsService;
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
public class ParamsService extends DtoService<ParamsDao, ParamsEntity, ParamsDTO> implements BaseParamsService {


    @Override
    public String getSystemContent(String code) {
        return getContent(UcConst.ParamsTypeEnum.SYSTEM.value(), null, null, code, null, null);
    }

    @Override
    public String getUserContent(Long userId, String code) {
        return getContent(UcConst.ParamsTypeEnum.USER.value(), null, userId, code, null, null);
    }

    @Override
    public String getTenantContent(String tenantCode, String code) {
        return getContent(UcConst.ParamsTypeEnum.TENANT.value(), tenantCode, null, code, null, null);
    }

    @Override
    public String getContent(Integer type, String tenantCode, Long userId, String code) {
        return getContent(type, tenantCode, userId, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    @Override
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

    @Override
    public JSONObject getSystemContentJson(String code) {
        return getSystemContentObject(code, JSONObject.class, null);
    }

    @Override
    public JSONObject getUserContentJson(Long userId, String code) {
        return getUserContentObject(userId, code, JSONObject.class, null);
    }

    @Override
    public JSONObject getTenantContentJson(String tenantCode, String code) {
        return getTenantContentObject(tenantCode, code, JSONObject.class, null);
    }

    @Override
    public <T> T getSystemContentObject(String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.SYSTEM.value(), null, null, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getUserContentObject(Long userId, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.USER.value(), null, userId, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getTenantContentObject(String tenantCode, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.TENANT.value(), tenantCode, null, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, Class<T> clazz, T defObj) {
        return getContentObject(type, tenantCode, userId, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue, Class<T> clazz, T defObj) {
        String content = getContent(type, tenantCode, userId, code, contentJsonKey, contentJsonValue);
        return JacksonUtils.jsonToPojo(content, clazz, defObj);
    }

}
