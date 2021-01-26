package com.nb6868.onexboot.api.modules.sys.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ParamEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.Map;

/**
 * 参数管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ParamService extends CrudService<ParamEntity, ParamDTO> {

    /**
     * 通过code获取参数
     * @param code
     * @return
     */
    ParamEntity getByCode(String code);

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code  参数编码
     */
    String getContent(String code);

    /**
     * 根据参数编码，获取参数的Map
     *
     * @param code  参数编码
     */
    Map<String, Object> getContentMap(String code);

    /**
     * 根据参数编码，获取参数的Map
     *
     * @param code  参数编码
     */
    JsonNode getContentJsonNode(String code);

    /**
     * 根据参数编码，获取合并后的map
     *
     * @param code  参数编码
     */
    Map<String, Object> getCombineContentMap(String code);

    /**
     * 根据参数编码，获取value的Object对象
     * @param code  参数编码
     * @param clazz  Object对象
     */
    <T> T getContentObject(String code, Class<T> clazz);

    /**
     * 清空缓存
     * @param key 缓存key
     * @return 操作结果
     */
    boolean clearCache(String key);

}
