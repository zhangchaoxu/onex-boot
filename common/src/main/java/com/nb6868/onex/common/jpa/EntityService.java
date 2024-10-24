package com.nb6868.onex.common.jpa;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.util.ConvertUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

/**
 * 基础服务实现类
 * 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型
 * see {https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/service/impl/ServiceImpl.java}
 * see {https://mybatis.plus/guide/crud-interface.html#service-crud-%E6%8E%A5%E5%8F%A3}
 * <p>
 * 进一步封装 CRUD
 * 采用 get 查询单行 remove 删除 list 查询集合 page 分页
 * 前缀命名方式区分 Mapper 层避免混淆
 *
 * @author hubin
 * @author Charles zhangchaoxu@gmail.com
 */
@SuppressWarnings("unchecked")
public class EntityService<M extends BaseDao<T>, T> extends CrudRepository<M, T> {

    /**
     * 获取当前Entity实例
     *
     * @return 当时Entity实例
     */
    protected T getEntityModel() {
        Class<T> modelClass = getEntityClass();
        try {
            return modelClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过条件删除内容
     *
     * @param wrapper 查询条件
     * @return 删除结果
     */
    public boolean logicDeleteByWrapper(UpdateChainWrapper<T> wrapper) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        return wrapper
                .set(tableInfo.getLogicDeleteFieldInfo().getColumn(), tableInfo.getLogicDeleteFieldInfo().getLogicDeleteValue())
                .update(getEntityModel());
    }

    /**
     * 下级记录数量
     *
     * @param column 关联字段
     * @param val    字段值
     * @return 数量
     */
    public long subCount(String column, Object val) {
        return count(new QueryWrapper<T>().eq(column, val));
    }

    /**
     * 是否存在下级记录
     *
     * @param column 关联字段
     * @param val    字段值
     * @return 是否存在
     */
    public boolean hasSub(String column, Object val) {
        return subCount(column, val) > 0;
    }

    /**
     * 是否存在对应字段同内容的记录
     *
     * @param id     可null
     * @param column 字段
     * @param val    字段值
     * @return 是否存在
     */
    public boolean hasDuplicated(Serializable id, String column, Object val) {
        return query().eq(column, val).ne(id != null, "id", id).exists();
    }

    /**
     * 是否存在主键id对应的记录
     *
     * @param id 查询主键
     */
    public boolean hasIdRecord(Serializable id) {
        if (ObjectUtil.isEmpty(id)) {
            return false;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return query().eq(keyProperty, id).exists();
    }

    /**
     * 通过指定字段获取记录
     *
     * @param column 字段名
     * @param val    内容值
     * @return 记录
     */
    public T getOneByColumn(String column, Object val) {
        return query().eq(column, val).last(Const.LIMIT_ONE).one();
    }

    /**
     * id是否只值,并且存在对应记录
     *
     * @param entity 查询实体
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Object getIdVal(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            return tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
        }
        return null;
    }

    /**
     * id是否只值,并且存在对应记录
     *
     * @param entity 查询实体
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean hasIdVal(T entity) {
        Object idVal = getIdVal(entity);
        return !(StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)));
    }

    /**
     * 转换分页
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> PageData<T> getPageData(IPage page, Class<T> target) {
        IPage<T> pageT = page.convert(source -> ConvertUtils.sourceToTarget(source, target));
        return new PageData<>(pageT);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     * 与saveOrUpdate区别在于，插入之前不做存在检查
     *
     * @param entity 实体对象
     * @return boolean
     */
    public boolean saveOrUpdateById(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) ? this.updateById(entity) : this.save(entity);
        }
        return false;
    }

    /**
     * 与saveOrUpdateBatch区别在于，插入之前不做存在检查
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatchById(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.getMapperClass(), this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            // 只检查id，不检查实体  CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity))
            return StringUtils.checkValNull(idVal);
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

}
