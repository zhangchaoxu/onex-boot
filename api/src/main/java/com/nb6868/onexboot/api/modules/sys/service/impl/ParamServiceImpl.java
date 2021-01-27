package com.nb6868.onexboot.api.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.ParamDao;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ParamEntity;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 参数管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ParamServiceImpl extends CrudServiceImpl<ParamDao, ParamEntity, ParamDTO> implements ParamService {

    @Autowired
    Cache paramCache;

    @Override
    public QueryWrapper<ParamEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ParamEntity>(new QueryWrapper<>(), params)
                .like("code", "code")
                .getQueryWrapper();
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ParamDTO dto, ParamEntity existedEntity, int type) {
        super.afterSaveOrUpdateDto(ret, dto, existedEntity, type);
        // 删除原缓存
        if (existedEntity != null) {
            paramCache.evictIfPresent(existedEntity.getCode());
        }
        // 插入新缓存
        paramCache.put(dto.getCode(), dto.getContent());
    }

    @Override
    public ParamEntity getByCode(String code) {
        return query().eq("code", code).last(Const.LIMIT_ONE).one();
    }

    @Override
    public String getContent(String code) {
        // 先从缓存读取
        String content = paramCache.get("content_" + code, String.class);
        if (ObjectUtils.isEmpty(content)) {
            content = query().select("content").eq("code",code).last(Const.LIMIT_ONE).oneOpt().map(ParamEntity::getContent).orElse(null);
            paramCache.put("content_" + code, content);
        }
        return content;
    }

    @Override
    public Map<String, Object> getContentMap(String code) {
        return JacksonUtils.jsonToMap(getContent(code));
    }

    @Override
    public Map<String, Object> getCombineContentMap(String code) {
        // 先从缓存读取
        String contentCombine = paramCache.get("content_combine_" + code, String.class);
        if (ObjectUtils.isEmpty(contentCombine)) {
            ParamEntity entity = query().select("content", "content_pri").eq("code",code).last(Const.LIMIT_ONE).one();
            if (entity == null) {
                return null;
            } else {
                Map<String, Object> map = JacksonUtils.combineJson(entity.getContentPri(), entity.getContent());
                paramCache.put("content_combine_" + code, JacksonUtils.pojoToJson(map));
                return map;
            }
        } else {
            return JacksonUtils.jsonToMap(contentCombine);
        }
    }

    @Override
    public <T> T getCombineContentObject(String code, Class<T> clazz) {
        // 先从缓存读取
        String contentCombine = null;//paramCache.get("content_combine_" + code, String.class);
        if (ObjectUtils.isEmpty(contentCombine)) {
            ParamEntity entity = query().select("content", "content_pri").eq("code",code).last(Const.LIMIT_ONE).one();
            if (entity == null) {
                return null;
            } else {
                T pojo = JacksonUtils.combineJsonToPojo(entity.getContent(), entity.getContentPri(),  clazz);
                paramCache.put("content_combine_" + code, JacksonUtils.pojoToJson(pojo));
                return pojo;
            }
        } else {
            return JacksonUtils.jsonToPojo(contentCombine, clazz);
        }
    }

    @Override
    public <T> T getContentObject(String code, Class<T> clazz) {
        return JacksonUtils.jsonToPojo(getContent(code), clazz);
    }

    @Override
    public boolean clearCache(String key) {
        if (ObjectUtils.isEmpty(key)) {
            paramCache.invalidate();
        } else {
            paramCache.evictIfPresent(key);
        }
        return true;
    }

}
