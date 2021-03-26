package com.nb6868.onexboot.api.modules.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nb6868.onexboot.api.modules.sys.dao.ShorturlDao;
import com.nb6868.onexboot.api.modules.sys.dto.ShorturlDTO;
import com.nb6868.onexboot.api.modules.sys.entity.ShorturlEntity;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShorturlService extends DtoService<ShorturlDao, ShorturlEntity, ShorturlDTO> {

    @Override
    public QueryWrapper<ShorturlEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ShorturlEntity>(new QueryWrapper<>(), params)
                .like("url", "url")
                .like("name", "name")
                .eq("code", "code")
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(ShorturlDTO dto, ShorturlEntity toSaveEntity, int type) {
        if (!StringUtils.isEmpty(toSaveEntity.getCode())) {
            AssertUtils.isTrue(hasDuplicated(toSaveEntity.getId(), "code", toSaveEntity.getCode()), ErrorCode.ERROR_REQUEST, "编码已存在");
        } else {
            toSaveEntity.setCode(StringUtils.convertBase10To62(IdWorker.getId()));
        }
    }

}
