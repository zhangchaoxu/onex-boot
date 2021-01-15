package com.nb6868.onexboot.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO CRUD基础服务类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class CrudServiceImpl<M extends BaseDao<T>, T, D> extends BaseServiceImpl<M, T> implements CrudService<T, D> {

    /**
     * 构建条件构造器
     */
    public QueryWrapper<T> getWrapper(String method, Map<String, Object> params) {
        return new QueryWrapper<>();
    }

    @SuppressWarnings("unchecked")
    protected Class<D> currentDtoClass() {
        return (Class<D>) ReflectionKit.getSuperClassGenericType(getClass(), 2);
    }

    @Override
    public PageData<D> pageDto(Map<String, Object> params) {
        return pageDto(getPage(params, null, false), getWrapper("page", params));
    }

    @Override
    public PageData<D> pageDto(IPage<T> page, Wrapper<T> queryWrapper) {
        IPage<T> iPage = page(page, queryWrapper);

        PageData<D> pageData = getPageData(iPage, currentDtoClass());
        pageData.setLimit((int) page.getSize());
        pageData.setPage((int) page.getCurrent());
        pageData.setLastPage(page.getSize() * page.getCurrent() >= page.getTotal());
        return pageData;
    }

    @Override
    public List<D> listDto(Map<String, Object> params) {
        List<T> entityList = list(getWrapper("list", params));

        return ConvertUtils.sourceToTarget(entityList, currentDtoClass());
    }

    @Override
    public List<?> listDto(Map<String, Object> params, Class<?> target) {
        List<T> entityList = list(getWrapper("list", params));

        return ConvertUtils.sourceToTarget(entityList, target);
    }

    @Override
    public List<D> listDto(Wrapper<T> queryWrapper) {
        List<T> entityList = list(queryWrapper);

        return ConvertUtils.sourceToTarget(entityList, currentDtoClass());
    }

    @Override
    public int count(Map<String, Object> params) {
        return count(getWrapper("count", params));
    }

    @Override
    public D getDtoById(Serializable id) {
        T entity = getById(id);
        return ConvertUtils.sourceToTarget(entity, currentDtoClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getSelectColumnById(Serializable id, String column) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(column)) {
            return null;
        }
        Map<String, Object> entity = getMap(new QueryWrapper<T>().select(column).eq("id", id).last(Const.LIMIT_ONE));
        if (entity == null) {
            return null;
        } else {
            return (E) entity.get(column);
        }
    }

    /**
     * 新增和修改之前的操作
     * @param dto 保存dto
     * @param type 0 保存 1 修改
     */
    protected void beforeSaveOrUpdateDto(D dto, int type) {
    }

    /**
     * 新增和修改之前的操作
     * @param dto 保存dto
     * @param toSaveEntity 待保存entity
     * @param type 0 保存 1 修改
     */
    protected void beforeSaveOrUpdateDto(D dto, T toSaveEntity, int type) {
        this.beforeSaveOrUpdateDto(dto, type);
    }

    /**
     * 新增和修改之间的操作
     * @param dto 保存dto
     * @param existedEntity 数据库中原记录
     * @param type 0 保存 1 修改
     */
    protected void inSaveOrUpdateDto(D dto, T existedEntity, int type) {

    }

    /**
     * 新增和修改之后的操作
     * @param ret 结果
     * @param dto 保存dto
     * @param existedEntity 数据库中原记录
     * @param type 0 保存 1 修改
     */
    protected void afterSaveOrUpdateDto(boolean ret, D dto, T existedEntity, int type) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDto(D dto) {
        T entity = ConvertUtils.sourceToTarget(dto, currentModelClass());
        // 检查id
        Object idVal = getIdVal(entity);
        if (ObjectUtils.isNotEmpty(idVal)) {
            throw new OnexException(ErrorCode.ID_NOT_NULL_IN_SAVE);
        }
        // 自定义操作前检查
        beforeSaveOrUpdateDto(dto, entity,0);
        boolean ret = save(entity);
        // copy主键值到dto
        BeanUtils.copyProperties(entity, dto);
        // 自定义操作后检查
        this.afterSaveOrUpdateDto(ret, dto, entity, 0);
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDto(D dto) {
        T entity = ConvertUtils.sourceToTarget(dto, currentModelClass());
        // 检查id
        Object idVal = getIdVal(entity);
        if (ObjectUtils.isEmpty(idVal)) {
            throw new OnexException(ErrorCode.ID_NULL_IN_UPDATE);
        }
        // 自定义操作前检查
        this.beforeSaveOrUpdateDto(dto, entity, 1);
        // 更新之前,先检查一下对应记录存不存在
        // 要直接使用hasIdVal,可能就会出现问题
        // 传了一个fake id,就变成保存了
        T existedEntity = getById((Serializable) idVal);
        AssertUtils.isEmpty(existedEntity, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 自定义操作前检查
        this.inSaveOrUpdateDto(dto, existedEntity, 1);
        // 更新数据
        boolean ret = updateById(entity);
        // 自定义操作后检查
        this.afterSaveOrUpdateDto(ret, dto, existedEntity, 1);
        return ret;
    }

    /**
     * 尽量避免使用批量和saveOrUpdate方法,按照实际需要调用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDtos(List<D> dtos) {
        List<T> entityList = ConvertUtils.sourceToTarget(dtos, currentModelClass());
        // copy主键值到dto
        BeanUtils.copyProperties(entityList, dtos);
        return saveBatch(entityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDtos(List<D> dtos) {
        List<T> entityList = ConvertUtils.sourceToTarget(dtos, currentModelClass());
        return updateBatchById(entityList);
    }

    /**
     * 尽量避免直接使用saveOrUpdate,按照实际需要调用save或者update
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateDto(D dto) {
        T entity = ConvertUtils.sourceToTarget(dto, currentModelClass());
        Object idVal = getIdVal(entity);
        if (ObjectUtils.isNotEmpty(idVal)) {
            return updateDto(dto);
        } else {
            return saveDto(dto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateDtos(List<D> dtos) {
        List<T> entityList = ConvertUtils.sourceToTarget(dtos, currentModelClass());
        boolean ret = saveOrUpdateBatch(entityList);
        // copy主键值到dto
        BeanUtils.copyProperties(entityList, dtos);
        return ret;
    }

}
