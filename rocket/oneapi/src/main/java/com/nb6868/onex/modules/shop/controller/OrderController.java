package com.nb6868.onex.modules.shop.controller;

import com.nb6868.onex.booster.pojo.ChangeStatusRequest;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.pay.dto.PayRequest;
import com.nb6868.onex.modules.shop.dto.OrderDTO;
import com.nb6868.onex.modules.shop.excel.OrderExcel;
import com.nb6868.onex.modules.shop.service.OrderItemService;
import com.nb6868.onex.modules.shop.service.OrderService;
import com.nb6868.onex.modules.sys.service.ParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("ShopOrder")
@RequestMapping("shop/order")
@Validated
@Api(tags="订单")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ParamService paramService;

    @DataFilter(tableAlias = "shop_order", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:order:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<OrderDTO> list = orderService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_order", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:order:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OrderDTO> page = orderService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:order:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        OrderDTO data = orderService.getDtoById(id);

        return new Result<OrderDTO>().success(data);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:order:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody OrderDTO dto) {
        orderService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:order:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        orderService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("shop:order:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        orderService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "shop_order", tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:order:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<OrderDTO> list = orderService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "订单", list, OrderExcel.class);
    }

    @PostMapping("cancelAndRefund")
    @ApiOperation("取消并退款")
    @LogOperation("取消并退款")
    @RequiresPermissions("shop:order:cancel")
    public Result<?> cancelAndRefund(@Validated(value = {DefaultGroup.class}) @RequestBody ChangeStatusRequest request) {
        orderService.cancelAndRefund(request);

        return new Result<>();
    }

    @PostMapping("cancel")
    @ApiOperation("取消")
    @LogOperation("取消")
    @RequiresPermissions("shop:order:cancel")
    public Result<?> cancel(@Validated(value = {DefaultGroup.class}) @RequestBody ChangeStatusRequest request) {
        boolean ret = orderService.cancel(request.getId(), request.getRemark());

        return new Result<>();
    }

    @PostMapping("refund")
    @ApiOperation("退款")
    @LogOperation("退款")
    @RequiresPermissions("shop:order:refund")
    public Result<?> refund(@NotNull(message = "{id.require}") @RequestParam Long id) {
        // 执行退款
        orderService.refund(id);

        return new Result<>();
    }

    @PostMapping("pay")
    @ApiOperation("支付")
    @LogOperation("支付")
    @AnonAccess
    public Result<?> pay(@Validated(value = {DefaultGroup.class}) @RequestBody PayRequest payRequest) {
        // 执行支付
        return new Result<>().success(orderService.pay(payRequest));
    }

}
