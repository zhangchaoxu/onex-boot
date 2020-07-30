package com.nb6868.onex.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.StringUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.sys.dao.ShorturlDao;
import com.nb6868.onex.modules.sys.dto.ShorturlDTO;
import com.nb6868.onex.modules.sys.entity.ShorturlEntity;
import com.nb6868.onex.modules.sys.service.ShorturlService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShorturlServiceImpl extends CrudServiceImpl<ShorturlDao, ShorturlEntity, ShorturlDTO> implements ShorturlService {

    @Override
    public QueryWrapper<ShorturlEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ShorturlEntity>(new QueryWrapper<>(), params)
                .like("url", "url")
                .like("name", "name")
                .eq("code", "code")
                .getQueryWrapper();
    }

    @Override
    public ShorturlEntity getByCode(String code) {
        return query().eq("code", code).last(Const.LIMIT_ONE).one();
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
