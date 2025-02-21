package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.params.ParamsProps;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.ParamsDao;
import com.nb6868.onex.uc.dto.ParamsDTO;
import com.nb6868.onex.uc.dto.ParamsSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.ParamsEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ParamsService extends DtoService<ParamsDao, ParamsEntity, ParamsDTO> implements BaseParamsService {

    @Autowired
    ParamsProps paramsProps;

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
        return getContent(UcConst.ParamsTypeEnum.SYSTEM.getCode(), null, null, code, null, null);
    }

    @Override
    public String getUserContent(Long userId, String code) {
        return getContent(UcConst.ParamsTypeEnum.USER.getCode(), null, userId, code, null, null);
    }

    @Override
    public String getTenantContent(String tenantCode, String code) {
        return getContent(UcConst.ParamsTypeEnum.TENANT.getCode(), tenantCode, null, code, null, null);
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
        return lambdaQuery().select(ParamsEntity::getContent)
                .eq(StrUtil.isNotBlank(tenantCode), ParamsEntity::getTenantCode, tenantCode)
                .eq(StrUtil.isNotBlank(code), ParamsEntity::getCode, code)
                .eq(null != type, ParamsEntity::getType, type)
                .eq(null != userId, ParamsEntity::getUserId, userId)
                .eq(StrUtil.isAllNotBlank(contentJsonKey, contentJsonValue), (SFunction<ParamsEntity, String>) paramsEntity -> "content->'$." + contentJsonKey + "'", contentJsonValue)
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
        return getContentObject(UcConst.ParamsTypeEnum.SYSTEM.getCode(), null, null, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getUserContentObject(Long userId, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.USER.getCode(), null, userId, code, null, null, clazz, defObj);
    }

    @Override
    public <T> T getTenantContentObject(String tenantCode, String code, Class<T> clazz, T defObj) {
        return getContentObject(UcConst.ParamsTypeEnum.TENANT.getCode(), tenantCode, null, code, null, null, clazz, defObj);
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
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public ParamsEntity saveOrUpdateByReq(ParamsSaveOrUpdateReq req) {
        // 检查请求
        AssertUtils.isTrue(req.getType().equals(UcConst.ParamsTypeEnum.USER.getCode()) && ObjectUtil.isNull(req.getUserId()), "用户参数需指定用户ID");
        AssertUtils.isTrue(req.getType().equals(UcConst.ParamsTypeEnum.TENANT.getCode()) && ObjectUtil.isNull(req.getTenantCode()), "租户参数需指定租户编码");
        // todo 检查用户租户是否存在
        if (req.getType().equals(UcConst.ParamsTypeEnum.SYSTEM.getCode())) {
            AssertUtils.isTrue(lambdaQuery()
                    .ne(req.hasId(), ParamsEntity::getId, req.getId())
                    .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.SYSTEM.getCode())
                    .eq(ParamsEntity::getCode, req.getCode())
                    .exists(), "编号不能重复");
        } else if (req.getType().equals(UcConst.ParamsTypeEnum.TENANT.getCode())) {
            AssertUtils.isTrue(lambdaQuery()
                    .ne(req.hasId(), ParamsEntity::getId, req.getId())
                    .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.TENANT.getCode())
                    .eq(ParamsEntity::getCode, req.getCode())
                    .eq(ParamsEntity::getTenantCode, req.getTenantCode())
                    .exists(), "编号不能重复");
        } else if (req.getType().equals(UcConst.ParamsTypeEnum.USER.getCode())) {
            AssertUtils.isTrue(lambdaQuery()
                    .ne(req.hasId(), ParamsEntity::getId, req.getId())
                    .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.USER.getCode())
                    .eq(ParamsEntity::getCode, req.getCode())
                    .eq(ParamsEntity::getUserId, req.getUserId())
                    .exists(), "编号不能重复");
        }
        // 转换数据格式
        ParamsEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, ParamsEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
    }

}
