package com.nb6868.onex.api.modules.shop.controller.app;

import com.nb6868.onex.api.modules.uc.user.SecurityUser;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.nb6868.onex.api.modules.shop.dto.CartDTO;
import com.nb6868.onex.api.modules.shop.entity.CartEntity;
import com.nb6868.onex.api.modules.shop.service.CartService;
import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("app/shop/cart")
@Validated
@Api(tags="购物车 app")
public class AppCartController {

    @Autowired
    private CartService cartService;

    @GetMapping("count")
    @ApiOperation("购物车商品数量")
    public Result<?> count() {
        int count = cartService.count();

        return new Result<>().success(count);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    public Result<?> list() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", 1);
        List<CartDTO> list = cartService.listDto(params);

        return new Result<>().success(list);
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
