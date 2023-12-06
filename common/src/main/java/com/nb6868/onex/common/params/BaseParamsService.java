package com.nb6868.onex.common.params;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

/**
 * 基础参数服务,实现方法见uc中的ParamsService
 */
@Service
public interface BaseParamsService {

    <T> T getSystemPropsObject(String code, Class<T> clazz, T defObj);

    JSONObject getSystemPropsJson(String code);

    String getSystemProps(String code);

    /**
     * 获得系统参数
     */
    JSONObject getSystemContentJson(String code);

    /**
     * 获得用户参数
     */
    JSONObject getUserContentJson(Long userId, String code);

    /**
     * 获得租户参数
     */
    JSONObject getTenantContentJson(String tenantCode, String code);

    /**
     * 获得系统参数
     */
    String getSystemContent(String code);

    /**
     * 获得用户参数
     */
    String getUserContent(Long userId, String code);

    /**
     * 获得租户参数
     */
    String getTenantContent(String tenantCode, String code);

    /**
     * 根据参数编码，获取参数的value值
     */
    String getContent(Integer type, String tenantCode, Long userId, String code);

    String getContent(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue);

    /**
     * 获得用户参数
     */
    <T> T getSystemContentObject(String code, Class<T> clazz, T defObj);

    /**
     * 获得用户参数
     */
    <T> T getUserContentObject(Long userId, String code, Class<T> clazz, T defObj);

    /**
     * 获得租户参数
     */
    <T> T getTenantContentObject(String tenantCode, String code, Class<T> clazz, T defObj);

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, Class<T> clazz, T defObj);

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param code 参数编码
     */
    <T> T getContentObject(Integer type, String tenantCode, Long userId, String code, String contentJsonKey, String contentJsonValue, Class<T> clazz, T defObj);

    JSONObject getContentJson(String tenantCode, Long userId, @NotNull String code);

}
