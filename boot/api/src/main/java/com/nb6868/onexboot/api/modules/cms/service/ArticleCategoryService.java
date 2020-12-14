package com.nb6868.onexboot.api.modules.cms.service;

import com.nb6868.onexboot.api.modules.cms.dto.ArticleCategoryDTO;
import com.nb6868.onexboot.api.modules.cms.entity.ArticleCategoryEntity;
import com.nb6868.onexboot.common.service.CrudService;

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
