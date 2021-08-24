package com.nb6868.onex.api.modules.crm.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.nb6868.onex.api.common.annotation.DataFilter;
import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.api.modules.crm.dto.ProductCategoryDTO;
import com.nb6868.onex.api.modules.crm.excel.ProductCategoryExcel;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.MsgResult;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.api.modules.crm.service.ProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CRM 产品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/crm/productCategory")
@Validated
@Api(tags = "CRM 产品类别")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @DataFilter(tableAlias = "crm_product_category",tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("crm:productCategory:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ProductCategoryDTO> list = productCategoryService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "crm_product_category",tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("crm:productCategory:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ProductCategoryDTO> page = productCategoryService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("crm:productCategory:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ProductCategoryDTO data = productCategoryService.getDtoById(id);

        return new Result<ProductCategoryDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("crm:productCategory:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ProductCategoryDTO dto) {
        productCategoryService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("crm:productCategory:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ProductCategoryDTO dto) {
        productCategoryService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("crm:productCategory:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        productCategoryService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("crm:productCategory:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        productCategoryService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @DataFilter(tableAlias = "crm_product_category",tenantFilter = true)
    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("crm:productCategory:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ProductCategoryDTO> list = productCategoryService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "CRM 产品类别", list, ProductCategoryExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("crm:productCategory:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<ProductCategoryExcel> list = ExcelUtils.importExcel(file, ProductCategoryExcel.class, params);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (ProductCategoryExcel item : list) {
            ProductCategoryDTO dto = ConvertUtils.sourceToTarget(item, ProductCategoryDTO.class);
            MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
            if (validateResult.isSuccess()) {
                try {
                    dto.setPid(0L);
                    productCategoryService.saveDto(dto);
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
