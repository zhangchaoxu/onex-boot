package com.nb6868.onex.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.sys.dao.RegionDao;
import com.nb6868.onex.api.modules.sys.dto.RegionDTO;
import com.nb6868.onex.api.modules.sys.dto.RegionTreeDTO;
import com.nb6868.onex.api.modules.sys.entity.RegionEntity;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.TreeUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<RegionTreeDTO> treeList(Map<String, Object> params) {
        List<RegionTreeDTO> dtoList = ConvertUtils.sourceToTarget(baseMapper.selectList(getWrapper("treeList", params)), RegionTreeDTO.class);
        return TreeUtils.build(dtoList);
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
