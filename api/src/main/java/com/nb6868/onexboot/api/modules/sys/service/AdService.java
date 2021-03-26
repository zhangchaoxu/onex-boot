package com.nb6868.onexboot.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.dao.AdDao;
import com.nb6868.onexboot.api.modules.sys.dto.AdDTO;
import com.nb6868.onexboot.api.modules.sys.entity.AdEntity;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AdService extends DtoService<AdDao, AdEntity, AdDTO> {

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
