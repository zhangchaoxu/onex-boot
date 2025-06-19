package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.RoleDao;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.dto.RoleSaveOrUpdateReq;
import com.nb6868.onex.uc.dto.RoleUpdateStateReq;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RoleService extends DtoService<RoleDao, RoleEntity, RoleDTO> {

    @Autowired
    MenuScopeService menuScopeService;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleUserService roleUserService;

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public RoleEntity saveOrUpdateByReq(RoleSaveOrUpdateReq req) {
        // 检查请求
        AssertUtils.isTrue(hasDuplicated(req.getId(), "code", req.getCode()), "编码[" + req.getCode() + "]已存在");
        // 转换数据格式
        RoleEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, RoleEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        // 解释一下为什么不在role状态disable的时候，将关系表都删除
        // 因为一旦删除了，后续角色重新激活，关系数据就丢了需要重新配置，这与用户操作体验不符合
        // 所以在shiro过滤器中加入了对角色状态的过滤逻辑
        AssertUtils.isFalse(ret, "数据更新保存失败");
        // 重新保存角色和菜单关系表
        menuService.saveOrUpdateByRoleIdAndMenuIds(entity.getId(), req.getMenuIdList());
        return entity;
    }

    /**
     * 更新状态
     */
    public boolean updateState(RoleUpdateStateReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 状态变更
        return lambdaUpdate().eq(RoleEntity::getId, req.getId()).set(RoleEntity::getState, req.getState()).update(new RoleEntity());
    }

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户id
     */
    public List<Long> getRoleIdListByUserId(@NotNull Long userId) {
        return getRoleIdListByUserId(userId, null);
    }

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId    用户id
     * @param roleState 角色状态，null表示不处理
     */
    public List<Long> getRoleIdListByUserId(@NotNull Long userId, Integer roleState) {
        List<RoleUserEntity> list = roleUserService.lambdaQuery()
                .select(RoleUserEntity::getRoleId)
                .eq(RoleUserEntity::getUserId, userId)
                .groupBy(RoleUserEntity::getRoleId)
                .list();
        List<Long> relRoleIdList = CollStreamUtil.toList(list, RoleUserEntity::getRoleId);
        // 判断roleId是否还有效
        if (ObjUtil.isNull(roleState)) {
            return relRoleIdList;
        } else {
            List<RoleEntity> roleList = lambdaQuery()
                    .select(RoleEntity::getId)
                    .in(relRoleIdList.size() > 1, RoleEntity::getId, relRoleIdList)
                    .eq(relRoleIdList.size() == 1, RoleEntity::getId, relRoleIdList.get(0))
                    .eq(RoleEntity::getState, roleState)
                    .list();
            return CollStreamUtil.toList(roleList, RoleEntity::getId);
        }
    }

    /**
     * 根据用户查询角色Res列表
     *
     * @param userId 用户id
     */
    public List<RoleEntity> getRoleListByUserId(@NotNull Long userId) {
        return getRoleListByUserId(userId, null);
    }

    /**
     * 根据用户查询角色Res列表
     *
     * @param userId    用户id
     * @param roleState 角色状态，null表示不处理
     */
    public List<RoleEntity> getRoleListByUserId(@NotNull Long userId, Integer roleState) {
        // 先获取id，为什么不在这里过滤state?因为下面还会过滤的
        List<Long> roleIdList = getRoleIdListByUserId(userId);
        // 再用id查
        return CollUtil.isEmpty(roleIdList) ? CollUtil.newArrayList() : lambdaQuery()
                .in(roleIdList.size() > 1, RoleEntity::getId, roleIdList)
                .eq(roleIdList.size() == 1, RoleEntity::getId, roleIdList.get(0))
                // 状态过滤
                .eq(ObjUtil.isNotNull(roleState), RoleEntity::getState, roleState)
                .list();
    }

    /**
     * 根据角色ID查询用户列表
     *
     * @param roleIds 角色id
     */
    public List<Long> getUserIdListByRoleIdList(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(roleUserService.lambdaQuery()
                .select(RoleUserEntity::getUserId)
                .in(RoleUserEntity::getRoleId, roleIds)
                .groupBy(RoleUserEntity::getUserId)
                .list(), RoleUserEntity::getUserId);
    }

    /**
     * 根据角色编码查询角色ID数组
     *
     * @param roleCodes 角色编码
     */
    public List<Long> getRoleIdListByRoleCodeList(List<String> roleCodes) {
        if (CollUtil.isEmpty(roleCodes)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(lambdaQuery()
                .select(RoleEntity::getId)
                .in(RoleEntity::getCode, roleCodes)
                .list(), RoleEntity::getId);
    }

    /**
     * 根据角色编码查询用户列表
     *
     * @param roleCodes 角色编码
     */
    public List<Long> getUserIdListByRoleCodeList(List<String> roleCodes) {
        if (CollUtil.isEmpty(roleCodes)) {
            return CollUtil.newArrayList();
        }
        // 先找到角色id
        List<Long> roleIds = getRoleIdListByRoleCodeList(roleCodes);
        // 在用角色ID找到用户id
        return getUserIdListByRoleIdList(roleIds);
    }

    /**
     * 通过code获得角色
     *
     * @param code 角色编码
     */
    public RoleEntity getByCode(@NotNull String code) {
        return lambdaQuery().eq(RoleEntity::getCode, code)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 删除数据本身及关联关系
     *
     * @param id 角色id
     */
    public void deleteAllById(@NotNull Long id) {
        // 删除角色
        removeById(id, false);
        // 删除角色菜单关联关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(id));
        // 删除角色用户关联关系
        roleUserService.deleteByRoleIdList(Collections.singletonList(id));
    }

}
