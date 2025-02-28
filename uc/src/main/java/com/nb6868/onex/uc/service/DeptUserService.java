package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.DeptUserDao;
import com.nb6868.onex.uc.entity.DeptEntity;
import com.nb6868.onex.uc.entity.DeptUserEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门用户关系表
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DeptUserService extends EntityService<DeptUserDao, DeptUserEntity> {

    @Autowired
    DeptService deptService;
    @Autowired
    UserService userService;

    /**
     * 更新用户部门关系
     *
     * @param userId  用户ID
     * @param deptIds 部门ID数组
     * @param type 关系类型
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByUserIdAndDeptIds(Long userId, List<Long> deptIds, Integer type) {
        AssertUtils.isFalse(userService.hasIdRecord(userId), "用户ID不存在");
        if (CollUtil.isEmpty(deptIds)) {
            // 删除用户所有部门关系
            remove(lambdaQuery().eq(DeptUserEntity::getUserId, userId).eq(DeptUserEntity::getType, type).getWrapper());
        } else {
            // 判断传入的部门否存在
            AssertUtils.isFalse(deptIds.size() == deptService.lambdaQuery().in(DeptEntity::getId, deptIds).count(), "请检查部门Id参数");
            deptIds.forEach(deptId -> {
                // 判断关系是否存在
                if (!lambdaQuery().eq(DeptUserEntity::getUserId, userId).eq(DeptUserEntity::getDeptId, deptId).eq(DeptUserEntity::getType, type).exists()) {
                    // 不存在的做保存
                    DeptUserEntity relEntity = new DeptUserEntity();
                    relEntity.setUserId(userId);
                    relEntity.setDeptId(deptId);
                    relEntity.setType(type);
                    save(relEntity);
                }
            });
            // 删除非指定范围内的其它的关系
            remove(lambdaQuery().eq(DeptUserEntity::getUserId, userId).notIn(DeptUserEntity::getDeptId, deptIds).eq(DeptUserEntity::getType, type).getWrapper());
        }
        return true;
    }

    /**
     * 更新用户部门关系
     *
     * @param deptId  部门ID
     * @param userIds 用户ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByDeptIdAndUserIds(Long deptId, List<Long> userIds, Integer type) {
        AssertUtils.isFalse(deptService.hasIdRecord(deptId), "部门ID不存在");
        if (CollUtil.isEmpty(userIds)) {
            // 删除部门所有该类型关系
            remove(lambdaQuery().eq(DeptUserEntity::getDeptId, deptId).eq(DeptUserEntity::getType, type).getWrapper());
        } else {
            // 判断传入的用户是否存在
            AssertUtils.isFalse(userIds.size() == userService.lambdaQuery().in(UserEntity::getId, userIds).count(), "请检查用户Id参数");
            userIds.forEach(userId -> {
                // 判断关系是否存在
                if (!lambdaQuery().eq(DeptUserEntity::getDeptId, deptId).eq(DeptUserEntity::getUserId, userId).eq(DeptUserEntity::getType, type).exists()) {
                    // 不存在的做保存
                    DeptUserEntity relEntity = new DeptUserEntity();
                    relEntity.setUserId(userId);
                    relEntity.setDeptId(deptId);
                    relEntity.setType(type);
                    save(relEntity);
                }
            });
            // 删除非指定范围内的其它的关系
            remove(lambdaQuery().eq(DeptUserEntity::getDeptId, deptId).notIn(DeptUserEntity::getUserId, userIds).eq(DeptUserEntity::getType, type).getWrapper());
        }
        return true;
    }

    /**
     * 根据部门ids，删除部门用户关系
     *
     * @param deptIds 部门ids
     */
    public boolean deleteByDeptIdList(List<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return true;
        }
        return remove(lambdaQuery().in(DeptUserEntity::getDeptId, deptIds).getWrapper());
    }

    /**
     * 根据用户id，删除角色用户关系
     *
     * @param userIds 用户ids
     */
    public boolean deleteByUserIdList(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return true;
        }
        return remove(lambdaQuery().in(DeptUserEntity::getUserId, userIds).getWrapper());
    }

}
