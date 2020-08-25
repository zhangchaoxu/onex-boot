package com.nb6868.onex.modules.cms.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.cms.dto.ArticleDTO;
import com.nb6868.onex.modules.cms.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 文章管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("cms/article")
@Validated
@Api(tags = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("cms:article:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ArticleDTO> list = articleService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("cms:article:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ArticleDTO> page = articleService.pageDto(params);

        return new Result<PageData<ArticleDTO>>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("cms:article:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ArticleDTO data = articleService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<ArticleDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:article:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ArticleDTO dto) {
        articleService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:article:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ArticleDTO dto) {
        articleService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:article:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        articleService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("cms:article:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        articleService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
