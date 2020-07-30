package com.nb6868.onex.modules.sys.service.impl;

import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.sys.dao.OssDao;
import com.nb6868.onex.modules.sys.dto.OssDTO;
import com.nb6868.onex.modules.sys.entity.OssEntity;
import com.nb6868.onex.modules.sys.service.OssService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OssServiceImpl extends CrudServiceImpl<OssDao, OssEntity, OssDTO> implements OssService {

    @Override
    public QueryWrapper<OssEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OssEntity>(new QueryWrapper<>(), params)
                .eq("url", "url")
                .getQueryWrapper();
    }

}
