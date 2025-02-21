package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.TenantDao;
import com.nb6868.onex.uc.dto.TenantDTO;
import com.nb6868.onex.uc.dto.TenantSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.TenantEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TenantService extends DtoService<TenantDao, TenantEntity, TenantDTO> {

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public TenantEntity saveOrUpdateByReq(TenantSaveOrUpdateReq req) {
        // 检查请求
        AssertUtils.isTrue(hasDuplicated(req.getId(), "code", req.getCode()), "编码[" + req.getCode() + "]已存在");
        // 转换数据格式
        TenantEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, TenantEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
    }

}
