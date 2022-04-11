package com.nb6868.onex.msg.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.msg.dao.MailTplDao;
import com.nb6868.onex.msg.dto.MailTplDTO;
import com.nb6868.onex.msg.entity.MailTplEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 邮件模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MailTplService extends DtoService<MailTplDao, MailTplEntity, MailTplDTO> {

    @Override
    public QueryWrapper<MailTplEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<MailTplEntity>(new QueryWrapper<>(), params)
                .eq("type", "type")
                .eq("code", "code")
                .like("name", "name")
                .getQueryWrapper();
    }

    /**
     * 通过编码查询模板
     */
    public MailTplEntity getByCode(String tenantCode, String code) {
        return query().eq("code", code)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    @Override
    protected void beforeSaveOrUpdateDto(MailTplDTO dto, int type) {
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "code", dto.getCode()), ErrorCode.ERROR_REQUEST, "编码已存在");
    }

}
