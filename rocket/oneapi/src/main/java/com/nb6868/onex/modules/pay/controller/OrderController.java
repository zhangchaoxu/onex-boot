package com.nb6868.onex.modules.pay.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.pay.dto.OrderDTO;
import com.nb6868.onex.modules.pay.excel.OrderExcel;
import com.nb6868.onex.modules.pay.service.OrderService;
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
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("PayOrder")
@RequestMapping("pay/order")
@Validated
@Api(tags = "支付订单")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("pay:order:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<OrderDTO> list = orderService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("pay:order:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OrderDTO> page = orderService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("pay:order:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        OrderDTO data = orderService.getDtoById(id);

        return new Result<OrderDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("pay:order:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody OrderDTO dto) {
        orderService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("pay:order:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody OrderDTO dto) {
        orderService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("pay:order:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        orderService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("pay:order:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        orderService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("pay:order:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<OrderDTO> list = orderService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "支付订单", list, OrderExcel.class);
    }

    /**
     * 调用方为微信
     * 本地址在统一下单接口中定义
     *
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7&index=8
     * https://pay.weixin.qq.com/wiki/doc/api/external/native.php?chapter=9_7
     */
    @ApiOperation(value = "微信订单支付回调通知")
    @LogOperation("微信订单支付回调通知")
    @PostMapping("/wxNotify")
    @AnonAccess
    public String wxNotify(@RequestBody String xmlData) {
        try {
            orderService.handleWxNotifyResult(WxPayOrderNotifyResult.fromXML(xmlData));
            return WxPayNotifyResponse.success("处理成功");
        } catch (WxPayException | OnexException e) {
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    /**
     * 调用方为支付宝
     */
    @ApiOperation(value = "支付宝订单支付回调通知")
    @LogOperation("支付宝订单支付回调通知")
    @PostMapping("/alipayNotify")
    @AnonAccess
    public String alipayNotify(@RequestBody String xmlData) {
        // todo
        return "";
    }

}
