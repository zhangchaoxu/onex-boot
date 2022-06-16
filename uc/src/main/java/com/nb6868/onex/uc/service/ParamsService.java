package com.nb6868.onex.uc.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.params.ParamsProps;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.ParamsDao;
import com.nb6868.onex.uc.dto.ParamsDTO;
import com.nb6868.onex.uc.entity.ParamsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * 租户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ParamsService extends DtoService<ParamsDao, ParamsEntity, ParamsDTO> implements BaseParamsService {

    @Autowired
    private ParamsProps paramsProps;

    @Override
    public <T> T getSystemPropsObject(String code, Class<T> clazz, T defObj) {
        return JacksonUtils.jsonToPojo(getSystemProps(code), clazz, defObj);
    }

    @Override
    public JSONObject getSystemPropsJson(String code) {
        return getSystemPropsObject(code, JSONObject.class, null);
    }

    @Override
    public String getSystemProps(String code) {
        String content = null;
        if (paramsProps != null && ObjectUtil.isNotEmpty(paramsProps.getConfigs())) {
            content = paramsProps.getConfigs().get(code);
        }
        if (StrUtil.isEmpty(content)) {
            content = getSystemContent(code);
        }
        return content;
    }

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
    public JSONObject getContentJson(String tenantCode, Long userId, @NotNull String code) {
        if (StrUtil.isNotBlank(tenantCode)) {
            return getTenantContentJson(tenantCode, code);
        } else if (null != userId) {
            return getUserContentJson(userId, code);
        } else {
            return getSystemContentJson(code);
        }
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

    /**
     * 新增或者更新数据,通过code来判断
     */
    public ParamsEntity saveOrUpdateUserParams(String code, String content, String scope, Long userId) {
        ParamsEntity params = query().eq("type", UcConst.ParamsTypeEnum.USER.value()).eq("code", code).eq("user_id", userId).last(Const.LIMIT_ONE).one();
        if (params != null) {
            params.setContent(content);
            params.setScope(scope);
            updateById(params);
        } else {
            params = new ParamsEntity();
            params.setContent(content);
            params.setScope(scope);
            params.setType(UcConst.ParamsTypeEnum.USER.value());
            params.setUserId(userId);
            params.setCode(code);
            save(params);
        }
        return params;
    }

    /**
     * 新增或者更新数据,通过code来判断
     */
    public ParamsEntity saveOrUpdateSystemParams(String code, String content, String scope) {
        ParamsEntity params = query().eq("type", UcConst.ParamsTypeEnum.SYSTEM.value()).eq("code", code).last(Const.LIMIT_ONE).one();
        if (params != null) {
            params.setContent(content);
            params.setScope(scope);
            updateById(params);
        } else {
            params = new ParamsEntity();
            params.setContent(content);
            params.setScope(scope);
            params.setType(UcConst.ParamsTypeEnum.SYSTEM.value());
            params.setCode(code);
            save(params);
        }
        return params;
    }

    /**
     * 新增或者更新数据,通过code来判断
     */
    public ParamsEntity saveOrUpdateTenantParams(String code, String content, String scope, String tenantCode) {
        ParamsEntity params = query().eq("type", UcConst.ParamsTypeEnum.TENANT.value()).eq("tenant_code", tenantCode).eq("code", code).last(Const.LIMIT_ONE).one();
        if (params != null) {
            params.setContent(content);
            params.setScope(scope);
            updateById(params);
        } else {
            params = new ParamsEntity();
            params.setContent(content);
            params.setScope(scope);
            params.setTenantCode(tenantCode);
            params.setType(UcConst.ParamsTypeEnum.TENANT.value());
            params.setCode(code);
            save(params);
        }
        return params;
    }

}
