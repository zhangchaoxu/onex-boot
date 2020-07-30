package com.nb6868.onex.modules.tms.controller;

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
import com.nb6868.onex.modules.tms.dto.WaybillItemDTO;
import com.nb6868.onex.modules.tms.entity.WaybillItemEntity;
import com.nb6868.onex.modules.tms.excel.WaybillItemExcel;
import com.nb6868.onex.modules.tms.service.WaybillItemService;
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
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("WaybillItem")
@RequestMapping("tms/waybillItem")
@Validated
@Api(tags = "TMS-运单明细")
public class WaybillItemController {

    @Autowired
    private WaybillItemService waybillItemService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("tms:waybillItem:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<WaybillItemDTO> list = waybillItemService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("tms:waybillItem:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<WaybillItemDTO> page = waybillItemService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("tms:waybillItem:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        WaybillItemDTO data = waybillItemService.getDtoById(id);

        return new Result<WaybillItemDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("tms:waybillItem:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody WaybillItemDTO dto) {
        waybillItemService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("tms:waybillItem:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody WaybillItemDTO dto) {
        waybillItemService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("updateQtyUnload")
    @ApiOperation("更新卸货数量")
    @LogOperation("更新卸货数量")
    @RequiresPermissions("tms:waybillItem:update")
    public Result<?> updateQtyUnload(@RequestBody WaybillItemDTO dto) {
        waybillItemService.update().set("qty_unload", dto.getQtyUnload()).eq("id", dto.getId()).update(new WaybillItemEntity());

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("tms:waybillItem:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        waybillItemService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("tms:waybillItem:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        waybillItemService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("tms:waybillItem:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<WaybillItemDTO> list = waybillItemService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "TMS-运单明细", list, WaybillItemExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("tms:waybillItem:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<WaybillItemExcel> list = ExcelUtils.importExcel(file, WaybillItemExcel.class, params);

        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (WaybillItemExcel item : list) {
            WaybillItemDTO dto = ConvertUtils.sourceToTarget(item, WaybillItemDTO.class);
            MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
            if (validateResult.isSuccess()) {
                try {
                    waybillItemService.saveDto(dto);
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
