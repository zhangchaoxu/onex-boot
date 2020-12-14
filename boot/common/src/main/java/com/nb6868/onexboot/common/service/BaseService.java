package com.nb6868.onexboot.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;

/**
 * 基础服务接口(https://mybatis.plus/guide/crud-interface.html#service-crud-%E6%8E%A5%E5%8F%A3)
 *
 * 进一步封装 CRUD
 * 采用 get 查询单行 remove 删除 list 查询集合 page 分页
 * 前缀命名方式区分 Mapper 层避免混淆
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 通过条件删除内容
     *
     * @param wrapper 查询条件
     * @return 删除结果
     */
    boolean logicDeleteByWrapper(Wrapper<T> wrapper);

    /**
     * 通过id数组删除
     *
     * @param idList 数组
     * @return 删除结果
     */
    boolean logicDeleteByIds(Collection<? extends Serializable> idList);

    /**
     * 通过id数删除
     *
     * @param id id
     * @return 删除结果
     */
    boolean logicDeleteById(Serializable id);

    /**
     * 下级记录数量
     *
     * @param column 关联字段
     * @param val    字段值
     * @return 数量
     */
    int subCount(String column, Object val);

    /**
     * 是否存在下级记录
     *
     * @param column 关联字段
     * @param val    字段值
     * @return 是否存在
     */
    boolean hasSub(String column, Object val);

    /**
     * 是否存在对应字段同内容的记录
     *
     * @param id     可null
     * @param column 字段
     * @param val    字段值
     * @return 是否存在
     */
    boolean hasDuplicated(Serializable id, String column, Object val);

    /**
     * 是否存在查询条件的的记录
     *
     * @param wrapper 查询条件
     * @return 删除结果
     */
    boolean hasRecord(Wrapper<T> wrapper);

    /**
     * 是否存在id主键,并且存在主键对应记录
     *
     * @param entity 查询条件
     * @return 是否有id主键值
     */
    boolean hasIdVal(T entity);

    /**
     * 获得id主键
     *
     * @param entity 查询条件
     * @return id值
     */
    Object getIdVal(T entity);

    /**
     * 是否存在主键id对应的记录
     * @param id 查询主键
     */
    boolean hasIdRecord(Serializable id);

}
