package com.nb6868.onex.api.modules.crm.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.nb6868.onex.api.modules.crm.service.BusinessProductService;
import com.nb6868.onex.api.modules.crm.service.BusinessService;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.api.modules.crm.dto.BusinessDTO;
import com.nb6868.onex.api.modules.crm.dto.BusinessProductDTO;
import com.nb6868.onex.api.modules.crm.excel.BusinessExcel;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
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
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/business")
@Validated
@Api(tags = "CRM商机")
public class BusinessController {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private BusinessProductService businessProductService;

    @DataSqlScope(tableAlias = "crm_business", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:business:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<BusinessDTO> list = businessService.listDto(params);

        return new Result<>().success(list);
    }

    @DataSqlScope(tableAlias = "crm_business", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:business:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<BusinessDTO> page = businessService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:business:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        BusinessDTO data = businessService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.RECORD_NOT_EXISTED);

        // 通过id获取相关产品信息
        List<BusinessProductDTO> productList = businessProductService.getDtoListByBusinessId(id);
        data.setProductList(productList);
        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:business:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BusinessDTO dto) {
        businessService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:business:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BusinessDTO dto) {
        businessService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:business:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        businessService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:business:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        businessService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataSqlScope(tableAlias = "crm_business", tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:business:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<?> entityList = businessService.listDto(params, BusinessExcel.class);

        //List<BusinessExcel> list = businessService.listDto(params);

        //return ConvertUtils.sourceToTarget(entityList, currentDtoClass());

        ExcelUtils.downloadExcel(response, "商机", new ExportParams(), BusinessExcel.class, entityList);
    }

}
