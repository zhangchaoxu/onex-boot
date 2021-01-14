package com.nb6868.onexboot.api.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.RegionDao;
import com.nb6868.onexboot.api.modules.sys.dto.RegionDTO;
import com.nb6868.onexboot.api.modules.sys.dto.RegionTreeDTO;
import com.nb6868.onexboot.api.modules.sys.entity.RegionEntity;
import com.nb6868.onexboot.api.modules.sys.service.RegionService;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.TreeUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RegionServiceImpl extends CrudServiceImpl<RegionDao, RegionEntity, RegionDTO> implements RegionService {

    @Override
    public QueryWrapper<RegionEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<RegionEntity>(new QueryWrapper<>(), params)
                .eq("id", "id")
                .eq("pid", "pid")
                .eq("deep", "deep")
                .like("name", "name")
                .getQueryWrapper();
    }

    @Override
    public List<RegionTreeDTO> treeList(Map<String, Object> params) {
        List<RegionTreeDTO> dtoList = ConvertUtils.sourceToTarget(baseMapper.selectList(getWrapper("treeList", params)), RegionTreeDTO.class);
        return TreeUtils.build(dtoList);
    }

    @Override
    public boolean deleteById(Long id) {
        return remove(new QueryWrapper<RegionEntity>().likeRight("id", id));
    }
}
