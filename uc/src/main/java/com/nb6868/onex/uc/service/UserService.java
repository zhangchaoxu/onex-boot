package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.ChangeStateReq;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.UserDao;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.DeptUserEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserService extends DtoService<UserDao, UserEntity, UserDTO> {

    @Autowired
    ShiroDao shiroDao;
    @Autowired
    MenuScopeService menuScopeService;
    @Autowired
    MenuService menuService;
    @Autowired
    TokenService tokenService;
    @Autowired
    RoleService roleService;
    @Autowired
    RoleUserService roleUserService;
    @Autowired
    DeptUserService deptUserService;

    /**
     * 构建条件查询器
     */
    public QueryWrapper<UserEntity> buildQueryWrapper(UserQueryReq req, String from) {
        // 拼接查询条件
        QueryWrapper<UserEntity> queryWrapper = QueryWrapperHelper.getPredicate(req, from);
        // 自定义条件, fixme这里有bug，应该是并集，不是合集
        if (CollUtil.isNotEmpty(req.getDeptIds()) || CollUtil.isNotEmpty(req.getRoleIds()) || CollUtil.isNotEmpty(req.getRoleCodes())) {
            // 查询条件带有部门或者角色
            // 总的userId数组
            Set<Long> userIds = new HashSet<>();
            // 查询部门id匹配
            if (CollUtil.isNotEmpty(req.getDeptIds())) {
                List<Long> matchedUserIdList = getUserIdListByDeptIds(req.getDeptIds(), UcConst.DeptUserTypeEnum.DEFAULT.getCode());
                if (CollUtil.isEmpty(matchedUserIdList)) {
                    queryWrapper.eq("id", -1);
                    return queryWrapper;
                } else {
                    if (CollUtil.isEmpty(userIds)) {
                        userIds.addAll(matchedUserIdList);
                    } else {
                        userIds = CollUtil.unionDistinct(userIds, matchedUserIdList);
                    }
                }
            }
            // 查询角色id匹配
            if (CollUtil.isNotEmpty(req.getRoleIds())) {
                List<Long> matchedUserIdList = getUserIdListByRoleIds(req.getRoleIds(), UcConst.RoleUserTypeEnum.DEFAULT.getCode());
                if (CollUtil.isEmpty(matchedUserIdList)) {
                    queryWrapper.eq("id", -1);
                    return queryWrapper;
                }  else {
                    if (CollUtil.isEmpty(userIds)) {
                        userIds.addAll(matchedUserIdList);
                    } else {
                        userIds = CollUtil.unionDistinct(userIds, matchedUserIdList);
                    }
                }
            }
            // 查询角色code匹配
            if (CollUtil.isNotEmpty(req.getRoleCodes())) {
                List<Long> matchedUserIdList = getUserIdListByRoleCodes(req.getRoleCodes(), UcConst.RoleUserTypeEnum.DEFAULT.getCode());
                if (CollUtil.isEmpty(matchedUserIdList)) {
                    queryWrapper.eq("id", -1);
                    return queryWrapper;
                } else {
                    if (CollUtil.isEmpty(userIds)) {
                        userIds.addAll(matchedUserIdList);
                    } else {
                        userIds = CollUtil.unionDistinct(userIds, matchedUserIdList);
                    }
                }
            }
            // 到这里，userIds不应该为空了
            queryWrapper.in("id", userIds);
        }
        return queryWrapper;
    }

    /**
     * 通过部门id获得部门下的所有用户id
     */
    public List<Long> getUserIdListByDeptIds(List<Long> deptIds, Integer type) {
        if (CollUtil.isEmpty(deptIds)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(deptUserService.lambdaQuery()
                .select(DeptUserEntity::getUserId)
                .in(deptIds.size() > 1, DeptUserEntity::getDeptId, deptIds)
                .eq(deptIds.size() == 1, DeptUserEntity::getDeptId, deptIds.get(0))
                .eq(ObjUtil.isNotNull(type), DeptUserEntity::getType, type)
                .groupBy(DeptUserEntity::getUserId)
                .list(), DeptUserEntity::getUserId);
    }

    /**
     * 通过角色id获得部门下的所有用户id
     */
    public List<Long> getUserIdListByRoleIds(List<Long> roleIds, Integer type) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(roleUserService.lambdaQuery()
                .select(RoleUserEntity::getUserId)
                .in(RoleUserEntity::getRoleId, roleIds)
                .in(roleIds.size() > 1, RoleUserEntity::getRoleId, roleIds)
                .eq(roleIds.size() == 1, RoleUserEntity::getRoleId, roleIds.get(0))
                .eq(ObjUtil.isNotNull(type), RoleUserEntity::getType, type)
                .groupBy(RoleUserEntity::getUserId)
                .list(), RoleUserEntity::getUserId);
    }

    /**
     * 通过角色编码数组获得部门下的所有用户id
     */
    public List<Long> getUserIdListByRoleCodes(List<String> roleCodes, Integer type) {
        List<Long> roleIds = roleService.getRoleIdListByRoleCodeList(roleCodes);
        return getUserIdListByRoleIds(roleIds, type);
    }

    /**
     * 通过部门id获得部门下的所有用户
     */
    public List<UserEntity> getUserListByDeptIds(List<Long> deptIds, Integer type) {
        if (CollUtil.isEmpty(deptIds)) {
            return CollUtil.newArrayList();
        }
        List<Long> userIdList = getUserIdListByDeptIds(deptIds, type);
        if (CollUtil.isEmpty(userIdList)) {
            return CollUtil.newArrayList();
        }
        return lambdaQuery()
                .in(userIdList.size() > 1, UserEntity::getId, userIdList)
                .eq(userIdList.size() == 1, UserEntity::getId, userIdList.get(0))
                .list();
    }

    /**
     * 通过角色id获得部门下的所有用户
     */
    public List<UserEntity> getUserListByRoleIds(List<Long> roleIds, Integer type) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }
        List<Long> userIdList = getUserIdListByRoleIds(roleIds, type);
        if (CollUtil.isEmpty(userIdList)) {
            return CollUtil.newArrayList();
        }
        return lambdaQuery()
                .in(userIdList.size() > 1, UserEntity::getId, userIdList)
                .eq(userIdList.size() == 1, UserEntity::getId, userIdList.get(0))
                .list();
    }

    /**
     * 更新用户角色关系
     */
    public void updateRole(UserUpdateRoleReq req) {
        // 判断用户
        AssertUtils.isFalse(hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        roleUserService.updateByUserIdAndRoleIds(req.getId(), req.getRoleIds(), ObjUtil.defaultIfNull(req.getType(), UcConst.RoleUserTypeEnum.DEFAULT.getCode()));
    }

    /**
     * 更新用户部门关系
     */
    public void updateDept(UserUpdateDeptReq req) {
        // 判断用户
        AssertUtils.isFalse(hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        deptUserService.updateByUserIdAndDeptIds(req.getId(), req.getDeptIds(), ObjUtil.defaultIfNull(req.getType(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()));
    }

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public UserEntity saveOrUpdateByReq(UserSaveOrUpdateReq req) {
        // 检查请求
        ShiroUser user = ShiroUtils.getUser();
        AssertUtils.isTrue(user.getType() > req.getType(), "无权创建高等级用户");
        AssertUtils.isTrue(req.getType() == UcConst.UserTypeEnum.DEPT_ADMIN.getCode() && StrUtil.isEmpty(req.getDeptCode()), "单位管理员需指定所在单位");
        // AssertUtils.isTrue(user.getDeptId() != null && dto.getDeptId() == null, "需指定所在单位");
        AssertUtils.isTrue(hasDuplicated(req.getId(), "username", req.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
        AssertUtils.isTrue(hasDuplicated(req.getId(), "mobile", req.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
        // 转换数据格式
        UserEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
            entity.setPassword(ObjectUtils.isEmpty(req.getPassword()) ? null : PasswordUtils.encode(req.getPassword()));
            entity.setPasswordRaw(ObjectUtils.isEmpty(req.getPassword()) ? null : PasswordUtils.aesEncode(req.getPassword(), Const.AES_KEY));
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, UserEntity.class);
            entity.setPassword(PasswordUtils.encode(req.getPassword()));
            entity.setPasswordRaw(PasswordUtils.aesEncode(req.getPassword(), Const.AES_KEY));
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        // 保存角色用户关系
        roleUserService.updateByUserIdAndRoleIds(req.getId(), req.getRoleIds(), UcConst.RoleUserTypeEnum.DEFAULT.getCode());
        return entity;
    }

    /**
     * 获取用户权限列表
     */
    public List<String> getUserPermissions(ShiroUser user) {
        List<String> permissionsList = user.isFullPermissions() ? shiroDao.getAllPermissionsList(user.getTenantCode()) : shiroDao.getPermissionsListByUserId(user.getId());
        List<String> set = new ArrayList<>();
        permissionsList.forEach(permissions -> set.addAll(StrUtil.splitTrim(permissions, ',')));
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public List<Long> getUserRoleIds(ShiroUser user) {
        return user.isFullRoles() ? shiroDao.getAllRoleIdList(user.getTenantCode()) : shiroDao.getRoleIdListByUserId(user.getId());
    }

    /**
     * 获取用户角色编码列表
     */
    public List<String> getUserRoleCodes(ShiroUser user) {
        return user.isFullRoles() ? shiroDao.getAllRoleCodeList(user.getTenantCode()) : shiroDao.getRoleCodeListByUserId(user.getId());
    }

    /**
     * 通过用户名获取用户
     *
     * @param tenantCode 租户编码
     * @param username   用户名
     * @return 用户
     */
    public UserEntity getByUsername(String tenantCode, @NotNull String username) {
        return lambdaQuery().eq(UserEntity::getUsername, username)
                .eq(StrUtil.isNotBlank(tenantCode), UserEntity::getTenantCode, tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 通过用户名获取用户
     *
     * @param tenantCode 租户编码
     * @param mobile     手机号
     * @return 用户
     */
    public UserEntity getByMobile(String tenantCode, @NotNull String mobile) {
        return lambdaQuery().eq(UserEntity::getMobile, mobile)
                .eq(StrUtil.isNotBlank(tenantCode), UserEntity::getTenantCode, tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 通过第三方id获得用户
     *
     * @param oauthId 第三方用户id
     */
    public UserEntity getByOauthId(String tenantCode, @NotNull String oauthId) {
        return lambdaQuery().eq(UserEntity::getOauthUserid, oauthId)
                .eq(StrUtil.isNotBlank(tenantCode), UserEntity::getTenantCode, tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 删除数据本身及关联关系
     *
     * @param ids 角色id
     */
    public void deleteAllByIds(List<Long> ids) {
        // 删除用户本身
        removeByIds(ids, false);
        // 删除用户-角色关系
        roleUserService.deleteByUserIdList(ids);
        // 删除用户-权限关系
        menuScopeService.deleteByUserIdList(ids);
        // 删除用户token
        tokenService.deleteByUserIdList(ids);
    }

    /**
     * 修改状态
     */
    public boolean changeState(ChangeStateReq req) {
        boolean ret = lambdaUpdate().set(UserEntity::getState, req.getState()).eq(UserEntity::getId, req.getId()).update(new UserEntity());
        if (ret && ObjUtil.equal(req.getState(), UcConst.UserStateEnum.DISABLE.getCode())) {
            // 锁定用户,将token注销
            tokenService.deleteByUserIdList(Collections.singletonList(req.getId()));
        }
        return ret;
    }

    /**
     * 修改用户的授权
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changeMenuScope(Long id, List<Long> menuIds) {
        menuService.saveOrUpdateByUserIdAndMenuIds(id, menuIds);
        return true;
    }

    /**
     * 修改密码
     *
     * @param id               用户ID
     * @param newPassword      新密码
     * @param passwordStoreKey 密码可逆加密的key
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String newPassword, String passwordStoreKey) {
        return lambdaUpdate()
                .eq(UserEntity::getId, id)
                .set(UserEntity::getPassword, PasswordUtils.encode(newPassword))
                .set(StrUtil.isNotBlank(passwordStoreKey), UserEntity::getPasswordRaw, PasswordUtils.aesEncode(newPassword, passwordStoreKey))
                .update(new UserEntity());
    }

    /**
     * 合并帐号,将mergeFrom数据合并到mergeTo
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean merge(String mergeTo, List<String> mergeFrom) {
        // 删除被合并帐号
        removeByIds(mergeFrom, false);
        // 将被删除业务数据中的create_id/update_id更新为mergeTo
        return true;
    }

    /**
     * 通过用户ids获得用户真实名列表
     */
    public List<String> getUsernameListByUserIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(lambdaQuery().select(UserEntity::getUsername).in(UserEntity::getId, ids).list(), UserEntity::getUsername);
    }

    /**
     * 通过用户ids获得用户手机号列表
     */
    public List<String> getMobileListByUserIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(lambdaQuery().select(UserEntity::getMobile).in(UserEntity::getId, ids).list(), UserEntity::getMobile);
    }

    /**
     * 通过用户ids获得用户真实姓名列表
     */
    public List<String> getRealNameListByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(lambdaQuery().select(UserEntity::getRealName).in(UserEntity::getId, ids).list(), UserEntity::getRealName);
    }

    /**
     * 通过用户ids获得用户真实姓名Join
     */
    public String getRealNameJoinByIds(List<Long> ids, CharSequence conjunction) {
        List<String> nameList = getRealNameListByIds(ids);
        return StrUtil.join(conjunction, nameList);
    }

}
