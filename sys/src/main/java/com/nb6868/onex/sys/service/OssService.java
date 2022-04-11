package com.nb6868.onex.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.sys.dao.OssDao;
import com.nb6868.onex.sys.dto.OssDTO;
import com.nb6868.onex.sys.entity.OssEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OssService extends DtoService<OssDao, OssEntity, OssDTO> {

    @Override
    public QueryWrapper<OssEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OssEntity>(new QueryWrapper<>(), params)
                .eq("url", "url")
                .getQueryWrapper();
    }

}
