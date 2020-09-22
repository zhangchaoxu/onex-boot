package com.nb6868.onex.modules.shop.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.shop.dto.SupplierDTO;
import com.nb6868.onex.modules.shop.excel.SupplierExcel;
import com.nb6868.onex.modules.shop.service.SupplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 供应商
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("shop/supplier")
@Validated
@Api(tags="供应商")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @DataFilter(tableAlias = "shop_supplier", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:supplier:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<SupplierDTO> list = supplierService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_supplier", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:supplier:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<SupplierDTO> page = supplierService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:supplier:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        SupplierDTO data = supplierService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<SupplierDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:supplier:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody SupplierDTO dto) {
        supplierService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:supplier:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody SupplierDTO dto) {
        supplierService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:supplier:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        supplierService.logicDeleteById(id);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:supplier:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<SupplierDTO> list = supplierService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "供应商", list, SupplierExcel.class);
    }

}
