package com.nb6868.onexboot.api.modules.cms.service.impl;

import com.nb6868.onexboot.api.modules.cms.entity.SiteEntity;
import com.nb6868.onexboot.api.modules.cms.service.ArticleService;
import com.nb6868.onexboot.api.modules.cms.service.SiteService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.cms.dao.SiteDao;
import com.nb6868.onexboot.api.modules.cms.dto.SiteDTO;
import com.nb6868.onexboot.api.modules.cms.service.ArticleCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SiteServiceImpl extends CrudServiceImpl<SiteDao, SiteEntity, SiteDTO> implements SiteService {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleCategoryService articleCategoryService;

    @Override
    public QueryWrapper<SiteEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<SiteEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .like("status", "status")
                .getQueryWrapper();
    }

    @Override
    protected void beforeSaveOrUpdateDto(SiteDTO dto, int type) {
        boolean hasRecord = hasDuplicated(dto.getId(), "code", dto.getCode());
        AssertUtils.isTrue(hasRecord, ErrorCode.ERROR_REQUEST, "编码已存在");
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, SiteDTO dto, SiteEntity existedEntity, int type) {
        if (ret && type == 1) {
            // 更新成功
            if (!StringUtils.equals(existedEntity.getCode(), dto.getCode())) {
                // 如果code发生变化,更新相关业务表中的site_code
                articleCategoryService.updateSiteCode(dto.getId(), dto.getCode());
                articleService.updateSiteCode(dto.getId(), dto.getCode());
            }
        }
    }

}
