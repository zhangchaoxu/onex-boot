package com.nb6868.onex.modules.sys.service.impl;

import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.sys.dao.AdDao;
import com.nb6868.onex.modules.sys.dto.AdDTO;
import com.nb6868.onex.modules.sys.entity.AdEntity;
import com.nb6868.onex.modules.sys.service.AdService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AdServiceImpl extends CrudServiceImpl<AdDao, AdEntity, AdDTO> implements AdService {

    @Override
    public QueryWrapper<AdEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<AdEntity>(new QueryWrapper<>(), params)
                .eq("position", "position")
                .like("name", "name")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

}
