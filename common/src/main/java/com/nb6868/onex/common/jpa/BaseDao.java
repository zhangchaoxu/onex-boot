package com.nb6868.onex.common.jpa;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;

/**
 * 通用CRUD封装BaseDao接口
 * 为 Mybatis-Plus 启动时自动解析实体表关系映射转换为 Mybatis 内部对象注入容器
 * 泛型 T 为任意实体对象
 * 参数 Serializable 为任意类型主键 Mybatis-Plus 不推荐使用复合主键约定每一张表都有自己的唯一 id 主键
 * 对象 Wrapper 为 条件构造器
 *
 * 原deleteByIdWithFill等方法由自带的deleteById替换
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface BaseDao<T> extends BaseMapper<T> {
    /**
     * @deprecated
     * 通过wrapper软删除
     * see {LogicDeleteByIdWithFill}
     *
     * @param entity  实体
     * @param wrapper wrapper
     * @return result 删除结果
     */
    Integer deleteByWrapperWithFill(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 根据 ID 查询条数
     *
     * @param id 主键ID
     */
    Integer selectCountById(Serializable id);
}
