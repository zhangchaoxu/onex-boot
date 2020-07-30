package com.nb6868.onex.modules.shop.controller.app;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.shop.dto.OrderDTO;
import com.nb6868.onex.modules.shop.dto.OrderItemDTO;
import com.nb6868.onex.modules.shop.dto.OrderOneClickRequest;
import com.nb6868.onex.modules.shop.entity.OrderEntity;
import com.nb6868.onex.modules.shop.service.OrderItemService;
import com.nb6868.onex.modules.shop.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * App订单管理
 *
 * @author Charles
 */
@RestController
@RequestMapping("/app/shop/order")
@Validated
@Api(tags = "用户管理")
public class AppOrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @PostMapping("oneClick")
    @ApiOperation("一键下单")
    @LogOperation("一键下单")
    public Result<?> oneClick(@Validated(value = {AddGroup.class, DefaultGroup.class}) @RequestBody OrderOneClickRequest request) {
        // 下单
        OrderEntity order = orderService.oneClick(request);

        return new Result<>().success(order);
    }

    /*@Transactional(rollbackFor = Exception.class)
    @PostMapping("oneClickAndPay")
    @ApiOperation("一键下单并支付")
    @LogOperation("一键下单并支付")
    public Result<?> oneClickAndPay(@Validated(value = {AddGroup.class, DefaultGroup.class}) @RequestBody OrderOneClickRequest request) {
        // 下单
        OrderEntity order = orderService.oneClick(request);
        // 支付
        Serializable result = orderService.pay(order.getId());
        return new Result<>().success(result);
    }

    @PostMapping("pay")
    @ApiOperation("支付")
    @LogOperation("支付")
    public Result<?> pay(@NotNull(message = "{id.require}") @RequestParam Long id) {
        // 支付
        Serializable result = orderService.pay(id);
        return new Result<>().success(result);
    }*/

    @GetMapping("info")
    @ApiOperation("信息")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        OrderDTO data = orderService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.RECORD_NOT_EXISTED);

        // 通过id获取相关产品信息
        List<OrderItemDTO> orderItemList = orderItemService.getDtoListByOrderId(id);
        data.setOrderItemList(orderItemList);

        return new Result<>().success(data);
    }

    @GetMapping("benefitRank")
    @ApiOperation("收益排行")
    @LogOperation("收益排行")
    @AnonAccess
    public Result<?> benefitRank(@RequestParam(value = "id", required = true) Long id) {
        return new Result<>().success(orderService.benefitRanking(id));
    }
}
