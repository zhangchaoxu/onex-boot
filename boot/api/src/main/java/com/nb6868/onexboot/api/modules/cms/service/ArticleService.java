package com.nb6868.onexboot.api.modules.cms.service;

import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.cms.dto.ArticleDTO;
import com.nb6868.onexboot.api.modules.cms.entity.ArticleEntity;

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

    /**
     * 统计分类下的内容
     * @param articleCategoryId
     */
    Integer countByArticleCategoryId(Long articleCategoryId);

}
