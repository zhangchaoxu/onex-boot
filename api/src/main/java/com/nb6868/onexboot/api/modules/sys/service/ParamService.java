package com.nb6868.onexboot.api.modules.sys.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.sys.entity.ParamEntity;

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
    ParamDTO getByCode(String code);

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code  参数编码
     */
    String getContent(String code);

    /**
     * 根据参数编码，获取value的Object对象
     * @param code  参数编码
     * @param clazz  Object对象
     */
    <T> T getContentObject(String code, Class<T> clazz);

    /**
     * 根据参数编码，获取value的JsonNode对象
     * @param code
     * @return
     */
    JsonNode getContentJsonNode(String code);

    /**
     * 根据参数编码，更新value
     * @param code  参数编码
     * @param value  参数值
     */
    void updateContentByCode(String code, String value);

    /**
     * 清空缓存
     * @param key 缓存key
     * @return 操作结果
     */
    boolean clearCache(String key);

}
