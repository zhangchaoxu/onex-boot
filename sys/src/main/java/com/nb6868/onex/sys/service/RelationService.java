package com.nb6868.onex.sys.service;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.sys.dao.RelationDao;
import com.nb6868.onex.sys.entity.RelationEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统-中间关系表
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RelationService extends EntityService<RelationDao, RelationEntity> {

    /**
     * 保存或修改
     *
     * @param leftId   左表ID
     * @param rightIds 右表ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateByLeftIdAndRightIds(@NotBlank String type, @NotNull Long leftId, List<Long> rightIds) {
        // 先删除关系
        remove(lambdaQuery()
                .eq(RelationEntity::getType, type)
                .eq(RelationEntity::getLeftId, leftId)
                .getWrapper());
        // 保存关系
        AtomicInteger sort = new AtomicInteger(0);
        CollUtil.distinct(rightIds).forEach(id -> {
            RelationEntity entity = new RelationEntity();
            entity.setType(type);
            entity.setSort(sort.incrementAndGet());
            entity.setLeftId(leftId);
            entity.setRightId(id);
            // 如果有保存name的需求，可以在这里
            save(entity);
        });
        return true;
    }

    /**
     * 获得左id关联的数量
     *
     * @param type   类型
     * @param leftId 左表ID
     * @return 数量
     */
    public Long getCountByLeftId(String type, Long leftId) {
        return lambdaQuery()
                .eq(RelationEntity::getType, type)
                .eq(RelationEntity::getLeftId, leftId)
                .count();
    }

    /**
     * 做的左id关联的内容列表
     *
     * @param type   类型
     * @param leftId 左表ID
     * @return 关联列表
     */
    public List<RelationEntity> getListByLeftId(String type, Long leftId) {
        return lambdaQuery()
                .eq(RelationEntity::getType, type)
                .eq(RelationEntity::getLeftId, leftId)
                .orderByAsc(RelationEntity::getSort, RelationEntity::getId)
                .list();
    }

    /**
     * 做的左id关联的右id数组
     *
     * @param type   类型
     * @param leftId 左表ID
     * @return 关联列表
     */
    public List<Long> getRightIdListByLeftId(String type, Long leftId) {
        return CollStreamUtil.toList(lambdaQuery()
                .select(RelationEntity::getRightId)
                .eq(RelationEntity::getType, type)
                .eq(RelationEntity::getLeftId, leftId)
                .orderByAsc(RelationEntity::getSort, RelationEntity::getId)
                .list(), RelationEntity::getRightId);
    }

}
