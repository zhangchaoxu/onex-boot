package com.nb6868.onexboot.api.modules.crm.controller;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onexboot.api.modules.crm.excel.BusinessProductExcel;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import com.nb6868.onexboot.api.modules.crm.service.BusinessProductService;
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
 * CRM商机-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/businessProduct")
@Validated
@Api(tags = "CRM商机-产品明细")
public class BusinessProductController {
    @Autowired
    private BusinessProductService businessProductService;

    @DataFilter(tableAlias = "crm_business_product",tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:businessProduct:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<BusinessProductDTO> list = businessProductService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "crm_business_product",tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:businessProduct:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<BusinessProductDTO> page = businessProductService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:businessProduct:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        BusinessProductDTO data = businessProductService.getDtoById(id);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:businessProduct:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BusinessProductDTO dto) {
        businessProductService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:businessProduct:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BusinessProductDTO dto) {
        businessProductService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:businessProduct:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        businessProductService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:businessProduct:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        businessProductService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "crm_business_product",tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:businessProduct:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<BusinessProductDTO> list = businessProductService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "商机-产品明细", list, BusinessProductExcel.class);
    }

}
