package com.nb6868.onexboot.api.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nb6868.onexboot.api.modules.sys.dao.ParamDao;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ParamEntity;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 参数管理
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class ParamServiceImpl extends CrudServiceImpl<ParamDao, ParamEntity, ParamDTO> implements ParamService {

    /**
     * 本地缓存
     * 设置一个有效时间10分钟
     */
    Cache<String, String> localCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(10, TimeUnit.DAYS).build();

    @Override
    public QueryWrapper<ParamEntity> getWrapper(String method, Map<String, Object> params) {
        UserDetail user = SecurityUser.getUser();
        return new WrapperUtils<ParamEntity>(new QueryWrapper<>(), params)
                .like("code", "code")
                .getQueryWrapper()
                .eq(user.getType() != UcConst.UserTypeEnum.ADMIN.value(), "type", 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDto(ParamDTO dto) {
        localCache.put("param_" + dto.getCode(), dto.getContent());
        return super.saveDto(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDto(ParamDTO dto) {
        localCache.put("param_" + dto.getCode(), dto.getContent());
        return super.updateDto(dto);
    }

    @Override
    public ParamDTO getByCode(String code) {
        UserDetail user = SecurityUser.getUser();
        ParamEntity entity = query()
                // .eq(user.getType() != UserTypeEnum.ADMIN.value(), "type", 1)
                .eq("code", code)
                .last(Const.LIMIT_ONE)
                .one();
        return ConvertUtils.sourceToTarget(entity, ParamDTO.class);
    }


    @Override
    public String getContent(String code) {
        // 先从缓存中读取
        String content = localCache.getIfPresent("param_" + code);
        if (StringUtils.isEmpty(content)) {
            content = query().select("content").eq("code",code).last(Const.LIMIT_ONE).oneOpt().map(ParamEntity::getContent).orElse(null);
            // 塞回缓存
            if (StringUtils.isNotEmpty(content)) {
                localCache.put("param_" + code, content);
            } else {
                localCache.invalidate("param_" + code);
            }
        }
        return content;
    }

    @Override
    public <T> T getContentObject(String code, Class<T> clazz) {
        String value = getContent(code);
        if (StringUtils.isNotBlank(value)) {
            return JacksonUtils.jsonToPojo(value, clazz);
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OnexException(ErrorCode.PARAMS_GET_ERROR);
        }
    }

    @Override
    public <T> T getContentObject(String code, Class<T> clazz, T defaultObject) {
        String value = getContent(code);
        return JacksonUtils.jsonToPojo(value, clazz);
    }

    @Override
    public JsonNode getContentJsonNode(String code) {
        String value = getContent(code);
        return JacksonUtils.jsonToNode(value);
    }

    @Override
    public void updateContentByCode(String code, String content) {
        update().set("content", content).eq("code", code);
    }

    @Override
    public boolean clearCache(String key) {
        if (StringUtils.isNotBlank(key)) {
            localCache.invalidate(key);
        } else {
            localCache.invalidateAll();
        }
        return true;
    }

}
