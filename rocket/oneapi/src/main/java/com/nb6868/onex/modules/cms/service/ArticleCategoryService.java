package com.nb6868.onex.modules.cms.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.cms.dto.ArticleCategoryDTO;
import com.nb6868.onex.modules.cms.entity.ArticleCategoryEntity;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ArticleCategoryService extends CrudService<ArticleCategoryEntity, ArticleCategoryDTO> {

    /**
     * 更新sitecode
     */
    boolean updateSiteCode(Long siteId, String newSiteCode);

    /**
     * 获取子类别数量
     */
    Integer childrenCount(Long id);

}
