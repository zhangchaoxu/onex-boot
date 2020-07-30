package com.nb6868.onex.modules.sys.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.sys.dto.RegionDTO;
import com.nb6868.onex.modules.sys.dto.RegionTreeDTO;
import com.nb6868.onex.modules.sys.entity.RegionEntity;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface RegionService extends CrudService<RegionEntity, RegionDTO> {

    List<RegionTreeDTO> treeList(Map<String, Object> params);

}
