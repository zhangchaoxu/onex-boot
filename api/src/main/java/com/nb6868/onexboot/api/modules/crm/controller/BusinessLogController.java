package com.nb6868.onexboot.api.modules.crm.controller;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.crm.dto.BusinessLogDTO;
import com.nb6868.onexboot.api.modules.crm.excel.BusinessLogExcel;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import com.nb6868.onexboot.api.modules.crm.service.BusinessLogService;
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
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("crm/businessLog")
@Validated
@Api(tags = "CRM商机记录")
public class BusinessLogController {
    @Autowired
    private BusinessLogService businessLogService;

    @DataFilter(tableAlias = "crm_business_log", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:businessLog:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<BusinessLogDTO> list = businessLogService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "crm_business_log", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:businessLog:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<BusinessLogDTO> page = businessLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:businessLog:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        BusinessLogDTO data = businessLogService.getDtoById(id);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:businessLog:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BusinessLogDTO dto) {
        businessLogService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:businessLog:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BusinessLogDTO dto) {
        businessLogService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:businessLog:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        businessLogService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:businessLog:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        businessLogService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "crm_business_log", tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:businessLog:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<BusinessLogDTO> list = businessLogService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "商机记录", list, BusinessLogExcel.class);
    }

}
