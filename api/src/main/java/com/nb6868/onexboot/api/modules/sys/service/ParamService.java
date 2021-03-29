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
            paramCache.evictIfPresent(existedEntity.getCode());
        }
        // 插入新缓存
        paramCache.put(dto.getCode(), dto.getContent());
    }

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code  参数编码
     */
    public String getContent(String code) {
        // 先从缓存读取
        String content = paramCache.get(code, String.class);
        if (ObjectUtils.isEmpty(content)) {
            content = query().select("content").eq("code",code).last(Const.LIMIT_ONE).oneOpt().map(ParamEntity::getContent).orElse(null);
            paramCache.put(code, content);
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
            paramCache.evictIfPresent(key);
        }
        return true;
    }

}
