package com.nb6868.onex.shop.modules.shop.controller;

import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.shop.modules.shop.dto.CartDTO;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import com.nb6868.onex.shop.modules.shop.service.CartService;
import com.nb6868.onex.shop.shiro.SecurityUser;
import com.nb6868.onex.shop.shiro.UserDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController("ShopCartController")
@RequestMapping("/shop/cart")
@Validated
@Api(tags = "购物车")
@Slf4j
public class CartController {
    @Autowired
    private CartService cartService;

    @DataFilter(tableAlias = "shop_cart", userFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<CartDTO> list = cartService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("count")
    @ApiOperation("购物车商品数量")
    public Result<?> count() {
        long count = cartService.count();

        return new Result<>().success(count);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody CartDTO dto) {
        cartService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody CartDTO dto) {
        cartService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        cartService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        cartService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @PostMapping("checkedAll")
    @ApiOperation("勾选所有")
    public Result<?> checkedAll() {
        UserDetail user = SecurityUser.getUser();
        cartService.update().set("checked", 1)
                .eq("checked", 0)
                .eq("state", 0)
                .eq("user_id", user.getId())
                .update(new CartEntity());
        // 是否有必要重新计算价格?
        return new Result<>();
    }

    @PostMapping("uncheckedAll")
    @ApiOperation("取消勾选所有")
    public Result<?> uncheckedAll() {
        UserDetail user = SecurityUser.getUser();
        cartService.update().set("checked", 0)
                .eq("checked", 1)
                .eq("state", 0)
                .eq("user_id", user.getId())
                .update(new CartEntity());
        return new Result<>();
    }

    @PostMapping("changeQty")
    @ApiOperation("修改数量")
    public Result<?> changeQty() {
        UserDetail user = SecurityUser.getUser();
        // 检查cart是存在
        // 检查是否允许修改这么多数量
        cartService.update().set("checked", 0)
                .eq("checked", 1)
                .eq("state", 0)
                .eq("user_id", user.getId())
                .update(new CartEntity());
        // 是否有必要重新计算价格?
        return new Result<>();
    }

}
