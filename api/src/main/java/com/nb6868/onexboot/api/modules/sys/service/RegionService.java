package com.nb6868.onexboot.api.modules.sys.service;

import com.nb6868.onexboot.api.modules.sys.dto.RegionDTO;
import com.nb6868.onexboot.api.modules.sys.dto.RegionTreeDTO;
import com.nb6868.onexboot.api.modules.sys.entity.RegionEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface RegionService extends CrudService<RegionEntity, RegionDTO> {

    List<RegionTreeDTO> treeList(Map<String, Object> params);

    /**
     * 通过id删除自身及子节点
     * @param id
     * @return 结果
     */
    boolean deleteById(Long id);

}
