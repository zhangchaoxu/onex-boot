package com.nb6868.onex.api.modules.cms.controller;

import com.nb6868.onex.api.modules.cms.service.ArticleCategoryService;
import com.nb6868.onex.api.modules.cms.service.ArticleService;
import com.nb6868.onex.api.modules.cms.dto.ArticleCategoryDTO;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/cms/articleCategory")
@Validated
@Api(tags = "文章类目")
public class ArticleCategoryController {
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleService articleService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("cms:articleCategory:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ArticleCategoryDTO> list = articleCategoryService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("cms:articleCategory:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ArticleCategoryDTO> page = articleCategoryService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("cms:articleCategory:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ArticleCategoryDTO data = articleCategoryService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:articleCategory:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ArticleCategoryDTO dto) {
        articleCategoryService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:articleCategory:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ArticleCategoryDTO dto) {
        articleCategoryService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:articleCategory:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        // 检查是否存在子类和文章
        AssertUtils.isTrue(articleCategoryService.childrenCount(id) > 0, "存在子类,不允许删除");
        AssertUtils.isTrue(articleService.countByArticleCategoryId(id) > 0, "存在文章,不允许删除");
        articleCategoryService.logicDeleteById(id);
        return new Result<>();
    }

}
