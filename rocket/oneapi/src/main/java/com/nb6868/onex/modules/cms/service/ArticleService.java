package com.nb6868.onex.modules.cms.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.cms.dto.ArticleDTO;
import com.nb6868.onex.modules.cms.entity.ArticleEntity;

import java.util.List;

/**
 * 文章
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ArticleService extends CrudService<ArticleEntity, ArticleDTO> {

    /**
     * 根据类别，查询文章
     *
     * @param categoryId 文章分类
     */
    List<ArticleDTO> getListByCategoryId(Long categoryId);

    /**
     * 根据类别，查询文章
     *
     * @param categoryIds 文章分类
     */
    List<ArticleDTO> getListByCategoryIds(List<Long> categoryIds);

    /**
     * 更新sitecode
     */
    boolean updateSiteCode(Long siteId, String newSiteCode);

    /**
     * 更新articleCategoryCode
     */
    boolean updateArticleCategoryCode(Long articleCategoryId, String articleCategoryCode);
}
