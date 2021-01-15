package com.nb6868.onexboot.common.service;

import com.nb6868.onexboot.common.pojo.PageData;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO CURD基础服务接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface CrudService<T, D> extends BaseService<T> {

    /**
     * 分页
     * @param params 查询条件
     * @return 分页数据
     */
    PageData<D> pageDto(Map<String, Object> params);

    /**
     * 分页
     * @param queryWrapper 查询条件
     * @return 分页数据
     */
    PageData<D> pageDto(IPage<T> page, Wrapper<T> queryWrapper);

    /**
     * 列表
     * @param params 查询条件
     * @return 列表数据
     */
    List<D> listDto(Map<String, Object> params);

    List<?> listDto(Map<String, Object> params, Class<?> target);

    /**
     * 列表
     * @param queryWrapper 查询条件
     * @return 列表数据
     */
    List<D> listDto(Wrapper<T> queryWrapper);

    /**
     * 条数
     * @param params 查询条件
     * @return 条数
     */
    int count(Map<String, Object> params);

    /**
     * 通过id获取结果
     * @param id 查询id
     * @return 结果
     */
    D getDtoById(Serializable id);

    /**
     * 通过id获取获取指定字段的值
     * @param id 查询id
     * @param column 指定字段
     * @return 结果
     */
    <E> E getSelectColumnById(Serializable id, String column);

    /**
     * 插入数据
     * @param dto 数据
     * @return 插入结果
     */
    boolean saveDto(D dto);

    /**
     * 批量插入
     * @param dtos 数据列表
     * @return 插入结果
     */
    boolean saveDtos(List<D> dtos);

    /**
     * 单条更新
     * @param dto 数据
     * @return 更新结果
     */
    boolean updateDto(D dto);

    /**
     * 批量更新
     * @param dtos 数据列表
     * @return 更新结果
     */
    boolean updateDtos(List<D> dtos);

    /**
     * 插入或者更新
     * @param dto 数据
     * @return 操作结果
     */
    boolean saveOrUpdateDto(D dto);

    /**
     * 批量插入或者更新
     * @param dtos 数据列表
     * @return 操作结果
     */
    boolean saveOrUpdateDtos(List<D> dtos);

}
