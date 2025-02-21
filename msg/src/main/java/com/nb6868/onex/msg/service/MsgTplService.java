package com.nb6868.onex.msg.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.dao.MsgTplDao;
import com.nb6868.onex.msg.dto.MsgTplDTO;
import com.nb6868.onex.msg.dto.MsgTplSaveOrUpdateReq;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MsgTplService extends DtoService<MsgTplDao, MsgTplEntity, MsgTplDTO> {

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public MsgTplEntity saveOrUpdateByReq(MsgTplSaveOrUpdateReq req) {
        // 检查请求
        AssertUtils.isTrue(hasDuplicated(req.getId(), "code", req.getCode()), ErrorCode.ERROR_REQUEST, "编码已存在");
        // 转换数据格式
        MsgTplEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, MsgTplEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
    }

    /**
     * 通过编码查询模板
     */
    public MsgTplEntity getByCode(String tenantCode, String code) {
        return query().eq("code", code)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

}
