package com.nb6868.onex.sys.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dao.RegionDao;
import com.nb6868.onex.sys.dto.RegionDTO;
import com.nb6868.onex.sys.dto.RegionPcdt;
import com.nb6868.onex.sys.entity.RegionEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RegionService extends DtoService<RegionDao, RegionEntity, RegionDTO> {

    @Override
    public QueryWrapper<RegionEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<RegionEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .eq("pid", "pid")
                .eq("deep", "deep")
                .like("name", "name")
                .getQueryWrapper();
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
        return remove(new QueryWrapper<RegionEntity>().likeRight("id", id));
    }

}
