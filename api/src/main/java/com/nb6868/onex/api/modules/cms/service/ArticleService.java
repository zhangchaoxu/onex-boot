package com.nb6868.onex.api.modules.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.cms.dao.ArticleDao;
import com.nb6868.onex.api.modules.cms.dto.ArticleDTO;
import com.nb6868.onex.api.modules.cms.entity.ArticleEntity;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 文章
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ArticleService extends DtoService<ArticleDao, ArticleEntity, ArticleDTO> {

    @Autowired
    ArticleCategoryService articleCategoryService;

    @Override
    public QueryWrapper<ArticleEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<ArticleEntity>(new QueryWrapper<>(), params)
                .like("author", "cms_article.author")
                .like("name", "cms_article.name")
                .like("content", "cms_article.content")
                .eq("articleCategoryId", "cms_article.article_category_id")
                .eq("articleCategoryCode", "cms_article.article_category_code")
                .eq("top", "cms_article.top")
                .eq("state", "cms_article.state")
                .eq("siteId", "cms_article.site_id")
                .eq("siteCode", "cms_article.site_code")
                .getQueryWrapper()
                .eq("cms_article.deleted", 0)
                .orderByAsc("sort");
    }

    /**
     * 根据类别，查询文章
     *
     * @param categoryId 文章分类
     */
    public List<ArticleDTO> getListByCategoryId(Long categoryId) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<ArticleEntity>()
                .eq("article_category_id", categoryId);

        return ConvertUtils.sourceToTarget(baseMapper.selectList(wrapper), ArticleDTO.class);
    }

    /**
     * 根据类别，查询文章
     *
     * @param categoryIds 文章分类
     */
    public List<ArticleDTO> getListByCategoryIds(List<Long> categoryIds) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<ArticleEntity>()
                .in("article_category_id", categoryIds);

        return ConvertUtils.sourceToTarget(baseMapper.selectList(wrapper), ArticleDTO.class);
    }

    /**
     * 更新sitecode
     */
    public boolean updateSiteCode(Long siteId, String newSiteCode) {
        return update().set("site_code", newSiteCode).eq("site_id", siteId).update(new ArticleEntity());
    }

    /**
     * 更新articleCategoryCode
     */
    public boolean updateArticleCategoryCode(Long articleCategoryId, String articleCategoryCode) {
        return update().set("article_category_code", articleCategoryCode).eq("article_category_id", articleCategoryId).update(new ArticleEntity());
    }

    /**
     * 统计分类下的内容
     */
    public Long countByArticleCategoryId(Long articleCategoryId) {
        return query().eq("article_category_id", articleCategoryId).count();
    }

}
