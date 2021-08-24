package com.nb6868.onex.api.modules.shop.controller;

import cn.hutool.core.lang.Dict;
import com.nb6868.onex.api.modules.uc.service.UserService;
import com.nb6868.onex.api.modules.shop.service.GoodsService;
import com.nb6868.onex.api.modules.shop.service.OrderService;
import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 商城首页数据数据
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/shop/index")
@Validated
public class IndexController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private GoodsService goodsService;

    @GetMapping("count")
    @ApiOperation("统计数据")
    public Result<?> count(@RequestParam Map<String, Object> params) {
        long orderCount = orderService.count(params);
        long userCount = userService.count(params);
        long goodsCount = goodsService.count(params);

        Dict data = Dict.create()
                .set("userCount", userCount)
                .set("orderCount", orderCount)
                .set("goodsCount", goodsCount);
        return new Result<>().success(data);
    }

}
