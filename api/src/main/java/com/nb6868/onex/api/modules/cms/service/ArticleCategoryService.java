package com.nb6868.onex.api.modules.cms.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.cms.dao.ArticleCategoryDao;
import com.nb6868.onex.api.modules.cms.dto.ArticleCategoryDTO;
import com.nb6868.onex.api.modules.cms.entity.ArticleCategoryEntity;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ArticleCategoryService extends DtoService<ArticleCategoryDao, ArticleCategoryEntity, ArticleCategoryDTO> {

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
        boolean hasRecord = query().eq(StrUtil.isNotBlank(dto.getCode()), "code", dto.getCode())
                .eq("site_id", dto.getSiteId())
                .ne(dto.getId() != null, "id", dto.getId()).exists();
        AssertUtils.isTrue(hasRecord, ErrorCode.ERROR_REQUEST, "编码已存在");
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, ArticleCategoryDTO dto, ArticleCategoryEntity existedEntity, int type) {
        if (1 == type && ret && !StrUtil.equalsIgnoreCase(existedEntity.getCode(), dto.getCode())) {
            // 更新成功, code发生变化,更新相关业务表中的code
            articleService.updateArticleCategoryCode(dto.getId(), dto.getCode());
        }
    }

    public boolean updateSiteCode(Long siteId, String newSiteCode) {
        return update().eq("site_id", siteId).set("site_code", newSiteCode).update(new ArticleCategoryEntity());
    }

    public Long childrenCount(Long id) {
        return query().eq("pid", id).count();
    }

}
