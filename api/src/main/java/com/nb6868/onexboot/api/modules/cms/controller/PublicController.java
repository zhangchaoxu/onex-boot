package com.nb6868.onexboot.api.modules.cms.controller;

import com.nb6868.onexboot.api.modules.cms.dto.SiteDTO;
import com.nb6868.onexboot.api.modules.cms.entity.SiteEntity;
import com.nb6868.onexboot.api.modules.cms.service.ArticleCategoryService;
import com.nb6868.onexboot.api.modules.cms.service.ArticleService;
import com.nb6868.onexboot.api.modules.cms.service.SiteService;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.ConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("CmsPublic")
@RequestMapping("cms/public")
@Validated
@Api(tags="CMS开放接口")
public class PublicController {

    @Autowired
    private SiteService siteService;
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleService articleService;

    @GetMapping("getSiteInfo")
    @ApiOperation("通过code获取站点详情")
    public Result<?> getSiteInfoByCode(@NotNull(message = "{code.require}") @RequestParam String code) {
        SiteEntity entity = siteService.getOneByColumn("code", code);
        SiteDTO dto = ConvertUtils.sourceToTarget(entity, SiteDTO.class);
        return new Result<>().success(dto);
    }



}
