package com.nb6868.onex.sys.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.sys.dao.RelationDao;
import com.nb6868.onex.sys.entity.RelationEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

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
     * @param leftId 左表ID
     * @param rightIds 右表ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateByLeftIdsAndRightIds(@NotNull  Long leftId, List<Long> rightIds) {
        // 先删除关系
        logicDeleteByWrapper(new QueryWrapper<RelationEntity>().eq("left_id", leftId));

        // 保存关系
        CollUtil.distinct(rightIds).forEach(id -> {
            RelationEntity entity = new RelationEntity();
            entity.setLeftId(leftId);
            entity.setRightId(id);
            save(entity);
        });
        return true;
    }

}
