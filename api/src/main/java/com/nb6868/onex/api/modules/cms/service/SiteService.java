package com.nb6868.onex.api.modules.cms.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.cms.dao.SiteDao;
import com.nb6868.onex.api.modules.cms.dto.SiteDTO;
import com.nb6868.onex.api.modules.cms.entity.SiteEntity;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SiteService extends DtoService<SiteDao, SiteEntity, SiteDTO> {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleCategoryService articleCategoryService;

    @Override
    public QueryWrapper<SiteEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<SiteEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .like("state", "state")
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
            if (!StrUtil.equalsIgnoreCase(existedEntity.getCode(), dto.getCode())) {
                // 如果code发生变化,更新相关业务表中的site_code
                articleCategoryService.updateSiteCode(dto.getId(), dto.getCode());
                articleService.updateSiteCode(dto.getId(), dto.getCode());
            }
        }
    }

}
