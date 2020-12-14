package com.nb6868.onexboot.api.modules.sys.service.impl;

import com.nb6868.onexboot.api.modules.sys.dao.OssDao;
import com.nb6868.onexboot.api.modules.sys.dto.OssDTO;
import com.nb6868.onexboot.api.modules.sys.entity.OssEntity;
import com.nb6868.onexboot.api.modules.sys.service.OssService;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
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
