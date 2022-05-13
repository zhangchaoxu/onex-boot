package com.nb6868.onex.sys.service;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.dao.MsgTplDao;
import com.nb6868.onex.sys.dto.MsgTplDTO;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import org.springframework.stereotype.Service;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MsgTplService extends DtoService<MsgTplDao, MsgTplEntity, MsgTplDTO> {

    /**
     * 通过编码查询模板
     */
    public MsgTplEntity getByCode(String tenantCode, String code) {
        return query().eq("code", code)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    @Override
    protected void beforeSaveOrUpdateDto(MsgTplDTO dto, int type) {
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "code", dto.getCode()), ErrorCode.ERROR_REQUEST, "编码已存在");
    }

}
