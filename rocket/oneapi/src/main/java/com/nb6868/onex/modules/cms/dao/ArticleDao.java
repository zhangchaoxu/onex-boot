package com.nb6868.onex.modules.cms.dao;

import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.cms.entity.ArticleEntity;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文章
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ArticleDao extends BaseDao<ArticleEntity> {

    @Select("SELECT cms_article.*, cms_article_category.name as article_category_name " +
            "FROM cms_article LEFT JOIN cms_article_category ON cms_article.article_category_id = cms_article_category.id " +
            "${ew.customSqlSegment}")
    @Override
    <E extends IPage<ArticleEntity>> E selectPage(@Param(Const.PAGE) E page, Wrapper<ArticleEntity> ew);

}
