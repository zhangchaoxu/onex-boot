package com.nb6868.onex.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.sys.dao.RegionDao;
import com.nb6868.onex.sys.dto.RegionDTO;
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
     * 通过id删除自身及子节点
     *
     * @param id
     * @return 结果
     */
    public boolean deleteById(Long id) {
        return remove(new QueryWrapper<RegionEntity>().likeRight("id", id));
    }

}
