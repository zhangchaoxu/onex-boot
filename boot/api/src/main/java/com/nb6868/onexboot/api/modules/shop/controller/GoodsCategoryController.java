package com.nb6868.onexboot.api.modules.shop.controller;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryDTO;
import com.nb6868.onexboot.api.modules.shop.dto.GoodsCategoryTreeDTO;
import com.nb6868.onexboot.api.modules.shop.service.GoodsCategoryService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.ParamUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("shop/goodsCategory")
@Validated
@Api(tags="商品类别")
public class GoodsCategoryController {

    @Autowired
    private GoodsCategoryService categoryService;

    @DataFilter(tableAlias = "shop_goods_category", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:goodsCategory:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<GoodsCategoryDTO> list = categoryService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_goods_category", tenantFilter = true)
    @GetMapping("tree")
    @ApiOperation("树表")
    @RequiresPermissions("shop:goodsCategory:list")
    public Result<?> tree(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<GoodsCategoryTreeDTO> tree = categoryService.tree(params);

        // 使用迭代器的删除方法删除
        if (ParamUtils.toBoolean(params.get("filterEmptyChild"), false)) {
            tree.removeIf(categoryTree -> ObjectUtils.isEmpty(categoryTree.getChildren()));
        }

        return new Result<>().success(tree);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:goodsCategory:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<GoodsCategoryDTO> page = categoryService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:goodsCategory:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        GoodsCategoryDTO data = categoryService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        data.setParentMenuList(categoryService.getParentMenuList(data.getPid()));

        return new Result<GoodsCategoryDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:goodsCategory:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody GoodsCategoryDTO dto) {
        categoryService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:goodsCategory:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody GoodsCategoryDTO dto) {
        categoryService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:goodsCategory:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        categoryService.logicDeleteById(id);

        return new Result<>();
    }

}
