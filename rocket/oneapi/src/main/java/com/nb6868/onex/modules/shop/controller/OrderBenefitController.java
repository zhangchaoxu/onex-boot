package com.nb6868.onex.modules.shop.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.MsgResult;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.ValidatorUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.shop.dto.OrderBenefitDTO;
import com.nb6868.onex.modules.shop.excel.OrderBenefitExcel;
import com.nb6868.onex.modules.shop.service.OrderBenefitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单收益明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("OrderBenefit")
@RequestMapping("shop/orderBenefit")
@Validated
@Api(tags = "订单收益明细")
public class OrderBenefitController {
    @Autowired
    private OrderBenefitService orderBenefitService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:orderBenefit:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<OrderBenefitDTO> list = orderBenefitService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:orderBenefit:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OrderBenefitDTO> page = orderBenefitService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:orderBenefit:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        OrderBenefitDTO data = orderBenefitService.getDtoById(id);

        return new Result<OrderBenefitDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:orderBenefit:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody OrderBenefitDTO dto) {
        orderBenefitService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:orderBenefit:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody OrderBenefitDTO dto) {
        orderBenefitService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:orderBenefit:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        orderBenefitService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("shop:orderBenefit:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        orderBenefitService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:orderBenefit:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<OrderBenefitDTO> list = orderBenefitService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "订单收益明细", list, OrderBenefitExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("shop:orderBenefit:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<OrderBenefitExcel> list = ExcelUtils.importExcel(file, OrderBenefitExcel.class, params);

        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (OrderBenefitExcel item : list) {
            OrderBenefitDTO dto = ConvertUtils.sourceToTarget(item, OrderBenefitDTO.class);
            MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
            if (validateResult.isSuccess()) {
                try {
                    orderBenefitService.saveDto(dto);
                    result.add(new MsgResult().success("导入成功"));
                } catch (Exception e) {
                    result.add(new MsgResult().error(ErrorCode.ERROR_REQUEST, e.getMessage()));
                }
            } else {
                result.add(validateResult);
            }
        }
        return new Result<>().success(result);
    }

}
