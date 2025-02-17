package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.PostDao;
import com.nb6868.onex.uc.dto.PostDTO;
import com.nb6868.onex.uc.dto.PostSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.PostEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 岗位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class PostService extends DtoService<PostDao, PostEntity, PostDTO> {

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public PostEntity saveOrUpdateByReq(PostSaveOrUpdateReq req) {
        // 检查请求
        // 转换数据格式
        PostEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, PostEntity.class);
        }
        // 处理数据
        saveOrUpdateById(entity);
        return entity;
    }

}
