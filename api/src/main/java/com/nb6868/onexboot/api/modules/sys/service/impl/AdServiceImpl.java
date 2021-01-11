package com.nb6868.onexboot.api.modules.sys.service.impl;

import com.nb6868.onexboot.api.modules.sys.dao.AdDao;
import com.nb6868.onexboot.api.modules.sys.dto.AdDTO;
import com.nb6868.onexboot.api.modules.sys.entity.AdEntity;
import com.nb6868.onexboot.api.modules.sys.service.AdService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
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
