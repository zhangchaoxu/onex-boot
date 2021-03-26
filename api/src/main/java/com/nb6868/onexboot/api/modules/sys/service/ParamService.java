package com.nb6868.onexboot.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.ParamDao;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ParamEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
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
public class ParamService extends DtoService<ParamDao, ParamEntity, ParamDTO> {

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
            paramCache.evictIfPresent("content_" + existedEntity.getCode());
            paramCache.evictIfPresent("content_combine_" + existedEntity.getCode());
        }
        // 插入新缓存
        paramCache.put("content_" + dto.getCode(), dto.getContent());
        paramCache.put("content_combine_" + dto.getCode(), JacksonUtils.mapToJson(JacksonUtils.combineJson(dto.getContentPri(), dto.getContent()), ""));
    }

    /**
     * 通过code获取参数
     * @param code
     * @return
     */
    public ParamEntity getByCode(String code) {
        return query().eq("code", code).last(Const.LIMIT_ONE).one();
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code  参数编码
     */
    public String getContent(String code) {
        // 先从缓存读取
        String content = paramCache.get("content_" + code, String.class);
        if (ObjectUtils.isEmpty(content)) {
            content = query().select("content").eq("code",code).last(Const.LIMIT_ONE).oneOpt().map(ParamEntity::getContent).orElse(null);
            paramCache.put("content_" + code, content);
        }
        return content;
    }

    /**
     * 根据参数编码，获取参数的Map
     *
     * @param code  参数编码
     */
    public Map<String, Object> getContentMap(String code) {
        return JacksonUtils.jsonToMap(getContent(code));
    }

    /**
     * 根据参数编码，获取合并后的map
     *
     * @param code  参数编码
     */
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

    /**
     * 根据参数编码，获取合并后的Object对象
     * @param code  参数编码
     * @param clazz  Object对象
     */
    public <T> T getCombineContentObject(String code, Class<T> clazz) {
        return JacksonUtils.mapToPojo(getCombineContentMap(code), clazz);
    }

    /**
     * 根据参数编码，获取value的Object对象
     * @param code  参数编码
     * @param clazz  Object对象
     */
    public <T> T getContentObject(String code, Class<T> clazz) {
        return JacksonUtils.jsonToPojo(getContent(code), clazz);
    }

    /**
     * 清空缓存
     * @param key 缓存key
     * @return 操作结果
     */
    public boolean clearCache(String key) {
        if (ObjectUtils.isEmpty(key)) {
            paramCache.invalidate();
        } else {
            paramCache.evictIfPresent("content_" + key);
            paramCache.evictIfPresent("content_combine_" + key);
        }
        return true;
    }

}
