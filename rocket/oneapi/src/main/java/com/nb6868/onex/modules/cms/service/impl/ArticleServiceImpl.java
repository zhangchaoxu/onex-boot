package com.nb6868.onex.modules.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.cms.dao.ArticleDao;
import com.nb6868.onex.modules.cms.dto.ArticleDTO;
import com.nb6868.onex.modules.cms.entity.ArticleEntity;
import com.nb6868.onex.modules.cms.service.ArticleCategoryService;
import com.nb6868.onex.modules.cms.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
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
public class ArticleServiceImpl extends CrudServiceImpl<ArticleDao, ArticleEntity, ArticleDTO> implements ArticleService {

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
                .eq("status", "cms_article.status")
                .eq("siteId", "cms_article.site_id")
                .eq("siteCode", "cms_article.site_code")
                .getQueryWrapper()
                .eq("cms_article.deleted", 0)
                .orderByAsc("sort");
    }

    @Override
    public List<ArticleDTO> getListByCategoryId(Long categoryId) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<ArticleEntity>()
                .eq("article_category_id", categoryId);

        return ConvertUtils.sourceToTarget(baseMapper.selectList(wrapper), ArticleDTO.class);
    }

    @Override
    public List<ArticleDTO> getListByCategoryIds(List<Long> categoryIds) {
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<ArticleEntity>()
                .in("article_category_id", categoryIds);

        return ConvertUtils.sourceToTarget(baseMapper.selectList(wrapper), ArticleDTO.class);
    }

    @Override
    protected void beforeSaveOrUpdateDto(ArticleDTO dto, int type) {
        boolean hasRecord = hasRecord(new QueryWrapper<ArticleEntity>().eq(StringUtils.isNotBlank(dto.getCode()), "code", dto.getCode())
                .eq("site_id", dto.getSiteId())
                .ne(dto.getId() != null, "id", dto.getId()));
        AssertUtils.isTrue(hasRecord, ErrorCode.ERROR_REQUEST, "编码已存在");
        // 调用阿里云-绿网-内容安全,鉴别文字内容
    }

    /*public static void testScanText(String scenes, String content) {
        com.aliyuncs.imageaudit.model.v20191230.ScanTextRequest scanTextRequest = new com.aliyuncs.imageaudit.model.v20191230.ScanTextRequest();
        List<ScanTextRequest.Tasks> taskss = new ArrayList<>();
        List<ScanTextRequest.Labels> labelss = new ArrayList<>();
        ScanTextRequest.Tasks tasks = new ScanTextRequest.Tasks();
        ScanTextRequest.Labels labels = new ScanTextRequest.Labels();
        tasks.setContent(content);
        taskss.add(tasks);
        labels.setLabel(scenes);
        labelss.add(labels);
        scanTextRequest.setLabelss(labelss);
        scanTextRequest.setTaskss(taskss);

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", "", "");
        IAcsClient client = new DefaultAcsClient(profile);
        ScanTextResponse resp;
        try {
            resp = client.getAcsResponse(scanTextRequest);
        } catch (Exception e) {
            // 服务端异常
            System.out.println(String.format("Exception:errMsg=%s", e.getMessage()));
            return;
        }
        System.out.println(JSON.toJSONString(resp.getData()));
    }*/

    @Override
    public boolean updateSiteCode(Long siteId, String newSiteCode) {
        return update(new UpdateWrapper<ArticleEntity>().eq("site_id", siteId).set("site_code", newSiteCode));
    }

    @Override
    public boolean updateArticleCategoryCode(Long siteId, String newSiteCode) {
        return update(new UpdateWrapper<ArticleEntity>().eq("article_category_id", siteId).set("article_category_code", newSiteCode));
    }

    @Override
    public Integer countByArticleCategoryId(Long articleCategoryId) {
        return query().eq("article_category_id", articleCategoryId).count();
    }
}
