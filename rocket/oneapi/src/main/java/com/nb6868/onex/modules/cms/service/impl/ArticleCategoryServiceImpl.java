package com.nb6868.onex.modules.cms.service.impl;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.cms.dao.ArticleCategoryDao;
import com.nb6868.onex.modules.cms.dto.ArticleCategoryDTO;
import com.nb6868.onex.modules.cms.entity.ArticleCategoryEntity;
import com.nb6868.onex.modules.cms.service.ArticleCategoryService;
import com.nb6868.onex.modules.cms.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ArticleCategoryServiceImpl extends CrudServiceImpl<ArticleCategoryDao, ArticleCategoryEntity, ArticleCategoryDTO> implements ArticleCategoryService {

    @Autowired
    ArticleService articleService;

    @Override
    public QueryWrapper<ArticleCategoryEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<ArticleCategoryEntity>(new QueryWrapper<>(), params)
                .like("cms_article_category.name", "name")
                .like("cms_article_category.sort", "sort")
                .like("cms_article_category.code", "code")
                .eq("cms_article_category.site_id", "siteId")
                .eq("cms_article_category.site_code", "siteCode")
                .getQueryWrapper()
                .orderByAsc("cms_article_category.sort");
    }

    @Override
    protected void beforeSaveOrUpdateDto(ArticleCategoryDTO dto, int type) {
        boolean hasRecord = hasRecord(new QueryWrapper<ArticleCategoryEntity>().eq(StringUtils.isNotBlank(dto.getCode()), "code", dto.getCode())
                .eq("site_id", dto.getSiteId())
                .ne(dto.getId() != null, "id", dto.getId()));
        AssertUtils.isTrue(hasRecord, ErrorCode.ERROR_REQUEST, "编码已存在");
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ArticleCategoryDTO dto, ArticleCategoryEntity existedEntity, int type) {
        if (1 == type && ret && !StringUtils.equals(existedEntity.getCode(), dto.getCode())) {
            // 更新成功, code发生变化,更新相关业务表中的code
            articleService.updateArticleCategoryCode(dto.getId(), dto.getCode());
        }
    }

    @Override
    public boolean updateSiteCode(Long siteId, String newSiteCode) {
        return update().eq("site_id", siteId).set("site_code", newSiteCode).update(new ArticleCategoryEntity());
    }

}
