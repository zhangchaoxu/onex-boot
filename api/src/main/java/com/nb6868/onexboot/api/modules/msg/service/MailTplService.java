package com.nb6868.onexboot.api.modules.msg.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.msg.dao.MailTplDao;
import com.nb6868.onexboot.api.modules.msg.dto.MailTplDTO;
import com.nb6868.onexboot.api.modules.msg.entity.MailTplEntity;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
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

    @Override
    protected void beforeSaveOrUpdateDto(MailTplDTO dto, int type) {
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "code", dto.getCode()), ErrorCode.ERROR_REQUEST, "编码已存在");
    }


}
