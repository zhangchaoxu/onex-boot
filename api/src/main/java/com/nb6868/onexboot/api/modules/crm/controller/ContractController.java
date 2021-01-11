package com.nb6868.onexboot.api.modules.crm.controller;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.crm.dto.ContractDTO;
import com.nb6868.onexboot.api.modules.crm.dto.ContractProductDTO;
import com.nb6868.onexboot.api.modules.crm.excel.ContractExcel;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import com.nb6868.onexboot.api.modules.crm.service.ContractProductService;
import com.nb6868.onexboot.api.modules.crm.service.ContractService;
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
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("crm/contract")
@Validated
@Api(tags = "CRM合同")
public class ContractController {
    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractProductService contractProductService;

    @DataFilter(tableAlias = "crm_contract",tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:contract:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ContractDTO> list = contractService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "crm_contract",tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:contract:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ContractDTO> page = contractService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:contract:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ContractDTO data = contractService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.RECORD_NOT_EXISTED);

        // 通过id获取相关产品信息
        List<ContractProductDTO> productList = contractProductService.getDtoListByContractId(id);
        data.setProductList(productList);
        return new Result<ContractDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:contract:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ContractDTO dto) {
        contractService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:contract:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ContractDTO dto) {
        contractService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:contract:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        contractService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:contract:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        contractService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "crm_contract",tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:contract:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ContractDTO> list = contractService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "合同", list, ContractExcel.class);
    }

}
