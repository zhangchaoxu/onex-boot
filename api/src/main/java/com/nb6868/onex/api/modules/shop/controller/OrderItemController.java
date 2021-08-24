package com.nb6868.onex.api.modules.shop.controller;

import com.nb6868.onex.api.common.annotation.DataFilter;
import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.api.modules.shop.dto.OrderItemDTO;
import com.nb6868.onex.api.modules.shop.excel.OrderItemExcel;
import com.nb6868.onex.api.modules.shop.service.OrderItemService;
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
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/shop/orderItem")
@Validated
@Api(tags = "订单明细")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @DataFilter(tableAlias = "shop_order_item", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:orderItem:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<OrderItemDTO> list = orderItemService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_order_item", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:orderItem:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OrderItemDTO> page = orderItemService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:orderItem:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        OrderItemDTO data = orderItemService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:orderItem:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody OrderItemDTO dto) {
        orderItemService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:orderItem:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody OrderItemDTO dto) {
        orderItemService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:orderItem:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        orderItemService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("shop:orderItem:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        orderItemService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:orderItem:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<OrderItemDTO> list = orderItemService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "订单明细", list, OrderItemExcel.class);
    }

}
