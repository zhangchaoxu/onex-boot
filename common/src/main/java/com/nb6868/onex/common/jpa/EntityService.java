package com.nb6868.onex.common.jpa;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.util.ConvertUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
public class EntityService<M extends BaseDao<T>, T> implements IService<T> {

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
                .update(currentModel());
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
     * 获取当前Entity实例
     *
     * @return 当时Entity实例
     */
    protected T currentModel() {
        Class<T> modelClass = currentModelClass();
        try {
            return modelClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
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
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.currentMapperClass(), this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal);
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    // [+] ServiceImpl
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected M baseMapper;

    @Override
    public M getBaseMapper() {
        Assert.notNull(this.baseMapper, "baseMapper can not be null");
        return this.baseMapper;
    }

    /**
     * @see #getEntityClass()
     */
    private Class<T> entityClass;

    @Override
    public Class<T> getEntityClass() {
        if(this.entityClass == null) {
            this.entityClass = (Class<T>) GenericTypeUtils.resolveTypeArguments(this.getMapperClass(), BaseMapper.class)[0];
        }
        return this.entityClass;
    }

    /**
     *  @see #currentMapperClass()
     */
    private Class<M> mapperClass;

    private volatile SqlSessionFactory sqlSessionFactory;

    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.sqlSessionFactory = MybatisUtils.getSqlSessionFactory(mybatisMapperProxy);
        }
        return this.sqlSessionFactory;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     * @deprecated 3.3.1
     */
    @Deprecated
    protected boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    /**
     * @return baseMapper 真实类型
     * @deprecated 3.5.7 {@link #getMapperClass()}
     */
    @Deprecated
    protected Class<M> currentMapperClass() {
        return this.getMapperClass();
    }

    /**
     * @return baseMapper 真实类型
     * @since 3.5.7
     */
    public Class<M> getMapperClass() {
        if (this.mapperClass == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.mapperClass = (Class<M>) mybatisMapperProxy.getMapperInterface();
        }
        return this.mapperClass;
    }

    /**
     * @return 实体类型
     * @deprecated 3.5.7 {@link #getEntityClass()}
     */
    @Deprecated
    protected Class<T> currentModelClass() {
        return getEntityClass();
    }


    /**
     * 批量操作 SqlSession
     *
     * @deprecated 3.3.0
     */
    @Deprecated
    protected SqlSession sqlSessionBatch() {
        return getSqlSessionFactory().openSession(ExecutorType.BATCH);
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     * @deprecated 3.3.0
     */
    @Deprecated
    protected void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     * @see #getSqlStatement(SqlMethod)
     * @deprecated 3.4.0
     */
    @Deprecated
    protected String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(getEntityClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(this.currentMapperClass(), sqlMethod);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean saveOrUpdate(T entity) {
        return getBaseMapper().insertOrUpdate(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.currentMapperClass(), this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return getBaseMapper().selectOne(queryWrapper, throwEx);
    }

    @Override
    public Optional<T> getOneOpt(Wrapper<T> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getBaseMapper().selectOne(queryWrapper, throwEx));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, getBaseMapper().selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    /**
     * 执行批量操作
     *
     * @param consumer consumer
     * @since 3.3.0
     * @deprecated 3.3.1 后面我打算移除掉 {@link #executeBatch(Collection, int, BiConsumer)} }.
     */
    @Deprecated
    protected boolean executeBatch(Consumer<SqlSession> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, consumer);
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @param <E>       泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, list, batchSize, consumer);
    }

    /**
     * 执行批量操作（默认批次提交数量{@link IService#DEFAULT_BATCH_SIZE}）
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @param <E>      泛型
     * @return 操作结果
     * @since 3.3.1
     */
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    @Override
    public boolean removeById(Serializable id, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id, useFill));
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(list));
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(list, useFill));
    }
    // [-] ServiceImpl

}
