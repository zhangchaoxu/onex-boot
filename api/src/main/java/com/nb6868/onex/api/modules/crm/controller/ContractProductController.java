package com.nb6868.onex.api.modules.crm.controller;

import com.nb6868.onex.api.modules.crm.service.ContractProductService;
import com.nb6868.onex.api.common.annotation.DataFilter;
import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.api.modules.crm.dto.ContractProductDTO;
import com.nb6868.onex.api.modules.crm.excel.ContractProductExcel;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
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
 * CRM合同-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/contractProduct")
@Validated
@Api(tags = "CRM合同-产品明细")
public class ContractProductController {
    @Autowired
    private ContractProductService contractProductService;

    @DataFilter(tableAlias = "crm_contract_product",tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:contractProduct:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ContractProductDTO> list = contractProductService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "crm_contract_product",tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:contractProduct:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ContractProductDTO> page = contractProductService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:contractProduct:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ContractProductDTO data = contractProductService.getDtoById(id);

        return new Result<ContractProductDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:contractProduct:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ContractProductDTO dto) {
        contractProductService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:contractProduct:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ContractProductDTO dto) {
        contractProductService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:contractProduct:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        contractProductService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:contractProduct:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        contractProductService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "crm_contract_product",tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:contractProduct:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ContractProductDTO> list = contractProductService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "合同-产品明细", list, ContractProductExcel.class);
    }

}
