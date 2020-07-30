package com.nb6868.onex.modules.aep.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.MsgResult;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.ValidatorUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.aep.dto.EnterpriseDTO;
import com.nb6868.onex.modules.aep.excel.EnterpriseExcel;
import com.nb6868.onex.modules.aep.service.EnterpriseService;
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
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Enterprise")
@RequestMapping("aep/enterprise")
@Validated
@Api(tags = "AEP-企业")
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("aep:enterprise:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<EnterpriseDTO> list = enterpriseService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("aep:enterprise:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<EnterpriseDTO> page = enterpriseService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("aep:enterprise:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        EnterpriseDTO data = enterpriseService.getDtoById(id);

        return new Result<EnterpriseDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("aep:enterprise:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody EnterpriseDTO dto) {
        enterpriseService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("aep:enterprise:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody EnterpriseDTO dto) {
        enterpriseService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("aep:enterprise:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        enterpriseService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("aep:enterprise:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        enterpriseService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("aep:enterprise:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<EnterpriseDTO> list = enterpriseService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "AEP-企业", list, EnterpriseExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("aep:enterprise:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<EnterpriseExcel> list = ExcelUtils.importExcel(file, EnterpriseExcel.class, params);

        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (EnterpriseExcel item : list) {
            EnterpriseDTO dto = ConvertUtils.sourceToTarget(item, EnterpriseDTO.class);
            MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
            if (validateResult.isSuccess()) {
                try {
                    enterpriseService.saveDto(dto);
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
