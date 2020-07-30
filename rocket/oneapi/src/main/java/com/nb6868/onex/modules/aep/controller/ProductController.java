package com.nb6868.onex.modules.aep.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.aep.dto.ProductDTO;
import com.nb6868.onex.modules.aep.excel.ProductExcel;
import com.nb6868.onex.modules.aep.service.ProductService;
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
 * AEP-产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Product")
@RequestMapping("aep/product")
@Validated
@Api(tags = "AEP-产品")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("aep:product:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ProductDTO> list = productService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("aep:product:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ProductDTO> page = productService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("aep:product:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ProductDTO data = productService.getDtoById(id);

        return new Result<ProductDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("aep:product:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ProductDTO dto) {
        productService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("aep:product:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ProductDTO dto) {
        productService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("aep:product:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        productService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("aep:product:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        productService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("aep:product:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ProductDTO> list = productService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "AEP-产品", list, ProductExcel.class);
    }

    @PostMapping("sync")
    @ApiOperation("同步")
    @LogOperation("同步")
    @RequiresPermissions("aep:product:sync")
    public Result<?> sync(@RequestParam(required = false) String searchValue) {
        boolean ret = productService.sync(searchValue);
        return new Result<>().setCode(ret ? ErrorCode.SUCCESS : ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
