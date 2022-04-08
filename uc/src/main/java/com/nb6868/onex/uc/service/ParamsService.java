package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
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
    public JSONObject getSystemContent(String code) {
        return getContent(UcConst.ParamsTypeEnum.SYSTEM.value(), null, null, code, null, null);
    }

    /**
     * 获得用户参数
     */
    public JSONObject getUserContent(Long userId, String code) {
        return getContent(UcConst.ParamsTypeEnum.USER.value(), null, userId, code, null, null);
    }

    /**
     * 获得租户参数
     */
    public JSONObject getTenantContent(String tenantCode, String code) {
        return getContent(UcConst.ParamsTypeEnum.TENANT.value(), tenantCode, null, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public JSONObject getContent(Integer type, String tenantCode, Long userId, String code) {
        return getContent(type, tenantCode, userId, code, null, null);
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    public JSONObject getContent(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue) {
        return query().select("content").eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode).eq(StrUtil.isNotBlank(code), "code", code).eq(null != type, "type", type).eq(null != userId, "user_id", userId).eq(StrUtil.isNotBlank(contentJsonKey) && StrUtil.isNotBlank(contentJsonValue), "content->'$." + contentJsonKey + "'", contentJsonValue).last(Const.LIMIT_ONE).oneOpt().map(ParamsEntity::getContent).orElse(new JSONObject());
    }

}
