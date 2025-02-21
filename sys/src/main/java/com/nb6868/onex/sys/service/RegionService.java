package com.nb6868.onex.sys.service;

import cn.hutool.core.bean.BeanUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dao.RegionDao;
import com.nb6868.onex.sys.dto.RegionDTO;
import com.nb6868.onex.sys.dto.RegionPcdt;
import com.nb6868.onex.sys.dto.RegionSaveOrUpdateReq;
import com.nb6868.onex.sys.entity.RegionEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RegionService extends DtoService<RegionDao, RegionEntity, RegionDTO> {

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public RegionEntity saveOrUpdateByReq(RegionSaveOrUpdateReq req) {
        // 检查请求
        // 转换数据格式
        RegionEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, RegionEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
    }

    /**
     * 通过id获得pcdt
     */
    public RegionPcdt getPcdtById(Long id) {
        if (null == id || 0 == id) {
            return null;
        } else if (id > 100000000) {
            // 街道
            Map<String, Object> map = getBaseMapper().getPcdtByT(id);
            return BeanUtil.toBean(map, RegionPcdt.class);
        } else if (id > 100000) {
            // 区县
            Map<String, Object> map = getBaseMapper().getPcdtByD(id);
            return BeanUtil.toBean(map, RegionPcdt.class);
        } else if (id > 1000) {
            // 城市
            Map<String, Object> map = getBaseMapper().getPcdtByC(id);
            return BeanUtil.toBean(map, RegionPcdt.class);
        } else {
            // 城市
            Map<String, Object> map = getBaseMapper().getPcdtByP(id);
            return BeanUtil.toBean(map, RegionPcdt.class);
        }
    }

    /**
     * 通过id删除自身及子节点
     *
     * @param id
     * @return 结果
     */
    public boolean deleteById(Long id) {
        return remove(lambdaQuery().likeRight(RegionEntity::getId, id).getWrapper());
    }

}
