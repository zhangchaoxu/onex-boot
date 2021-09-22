package com.nb6868.onex.shop.modules.shop.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.shop.modules.shop.ShopConst;
import com.nb6868.onex.shop.modules.shop.dto.CartDTO;
import com.nb6868.onex.shop.modules.shop.dto.CartQtyChangeRequest;
import com.nb6868.onex.shop.modules.shop.entity.CartEntity;
import com.nb6868.onex.shop.modules.shop.service.CartService;
import com.nb6868.onex.shop.shiro.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@RestController("ShopCartController")
@RequestMapping("/shop/cart")
@Validated
@Api(tags = "购物车")
@Slf4j
public class CartController {
    @Autowired
    private CartService cartService;

    @ApiOperationSupport(order = 10)
    @GetMapping("list")
    @ApiOperation("列表")
    public Result<?> list() {
        Long userId = SecurityUser.getUserId();
        // 按条件获得列表
        List<CartEntity> entityList = cartService.query()
                .eq("state", 1)
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .list();
        // 转成dto
        List<CartDTO> dtoList = ConvertUtils.sourceToTarget(entityList, CartDTO.class);
        // 返回列表和总价格
        BigDecimal price = cartService.calcCartTotalPrice();
        Dict dict = Dict.create().set("list", dtoList).set("price", price);
        return new Result<>().success(dict);
    }

    @ApiOperationSupport(order = 20)
    @GetMapping("count")
    @ApiOperation("购物车商品数量")
    public Result<?> count() {
        Long userId = SecurityUser.getUserId();
        // 按条件获得总数
        Long count = cartService.query()
                .eq("state", 1)
                .eq("user_id", userId)
                .count();
        return new Result<>().success(count);
    }

    @ApiOperationSupport(order = 30)
    @PostMapping("save")
    @ApiOperation("保存(加入购物车)")
    @LogOperation("保存(加入购物车)")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody CartDTO dto) {
        Long userId = SecurityUser.getUserId();
        // 检查当前购物车中数量，避免购物车内数量过大
        Long count = cartService.count(userId);
        if (count >= ShopConst.CART_MAX_LIMIT) {
            return new Result<>().error(StrUtil.format("购物车商品数最多{}个", ShopConst.CART_MAX_LIMIT));
        }
        CartEntity entity = ConvertUtils.sourceToTarget(dto, CartEntity.class);
        entity.setUserId(userId);
        entity.setState(0);
        entity.setChecked(1);
        // 保存数据
        cartService.save(entity);
        // 返回数据和总价格
        BigDecimal price = cartService.calcCartTotalPrice();
        Dict dict = Dict.create().set("entity", entity).set("price", price);
        return new Result<>().success(dict);
    }

    @ApiOperationSupport(order = 40)
    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除(移出购物车)")
    @LogOperation("批量删除(移出购物车)")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        Long userId = SecurityUser.getUserId();
        // 不做检查,直接删除
        cartService.update()
                .set("deleted", 1)
                .eq("user_id", userId)
                .in("id", ids)
                .update(new CartEntity());

        return new Result<>();
    }

    @ApiOperationSupport(order = 50)
    @PostMapping("checkedAll")
    @ApiOperation("勾选所有")
    public Result<?> checkedAll() {
        Long userId = SecurityUser.getUserId();
        cartService.update()
                .set("checked", 1)
                .eq("checked", 0)
                .eq("state", 0)
                .eq("user_id", userId)
                .update(new CartEntity());
        // 返回总价格
        BigDecimal price = cartService.calcCartTotalPrice();
        Dict dict = Dict.create().set("price", price);
        return new Result<>().success(dict);
    }

    @ApiOperationSupport(order = 60)
    @PostMapping("uncheckedAll")
    @ApiOperation("取消勾选所有")
    public Result<?> uncheckedAll() {
        Long userId = SecurityUser.getUserId();
        cartService.update().set("checked", 0)
                .eq("checked", 1)
                .eq("state", 0)
                .eq("user_id", userId)
                .update(new CartEntity());

        // 返回总价格
        BigDecimal price = cartService.calcCartTotalPrice();
        Dict dict = Dict.create().set("price", price);
        return new Result<>().success(dict);
    }

    @ApiOperationSupport(order = 70)
    @PostMapping("changeQty")
    @ApiOperation("修改数量")
    public Result<?> changeQty(@Validated(value = {DefaultGroup.class}) @RequestBody CartQtyChangeRequest request) {
        Long userId = SecurityUser.getUserId();
        // 检查cart和商品是否存在
        CartEntity entity = cartService.query()
                .eq("state", 0)
                .eq("goods_id", request.getGoodsId())
                .eq("user_id", userId)
                .last(Const.LIMIT_ONE)
                .one();
        AssertUtils.isNull(entity, "购物车不存在该商品");
        // todo 检查商品库存和购买数量限制
        cartService.update().set("checked", 0)
                .eq("checked", 1)
                .eq("state", 0)
                .eq("user_id", userId)
                .update(new CartEntity());

        // 返回总价格
        BigDecimal price = cartService.calcCartTotalPrice();
        Dict dict = Dict.create().set("price", price);
        return new Result<>().success(dict);
    }

}
