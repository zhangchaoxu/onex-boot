package com.nb6868.onex.modules.tms.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.tms.dto.WaybillDTO;
import com.nb6868.onex.modules.tms.dto.WaybillItemDTO;
import com.nb6868.onex.modules.tms.entity.WaybillEntity;
import com.nb6868.onex.modules.tms.excel.WaybillExcel;
import com.nb6868.onex.modules.tms.service.WaybillItemService;
import com.nb6868.onex.modules.tms.service.WaybillService;
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
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Waybill")
@RequestMapping("tms/waybill")
@Validated
@Api(tags = "TMS-运单")
public class WaybillController {

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private WaybillItemService waybillItemService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("tms:waybill:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<WaybillDTO> list = waybillService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("tms:waybill:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<WaybillDTO> page = waybillService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("tms:waybill:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        WaybillDTO data = waybillService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        // 通过id获取相关产品信息
        List<WaybillItemDTO> waybillItems = waybillItemService.getDtoListByWaybillId(id);
        data.setWaybillItemList(waybillItems);
        data.setWaybillItemCount(waybillItems.size());

        return new Result<WaybillDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("tms:waybill:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody WaybillDTO dto) {
        waybillService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("tms:waybill:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody WaybillDTO dto) {
        waybillService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("updateCarrierToDate")
    @ApiOperation("更新到港日")
    @LogOperation("更新到港日")
    @RequiresPermissions("tms:waybill:update")
    public Result<?> updateCarrierToDate(@RequestBody WaybillDTO dto) {
        waybillService.update().set("carrier_to_date", dto.getCarrierToDate()).eq("id", dto.getId()).update(new WaybillEntity());

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("tms:waybill:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        waybillService.logicDeleteById(id);
        waybillItemService.deleteByWaybillId(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("tms:waybill:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        waybillService.logicDeleteByIds(ids);

        for (Long id : ids) {
            waybillItemService.deleteByWaybillId(id);
        }

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("tms:waybill:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<WaybillDTO> list = waybillService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "TMS-运单", list, WaybillExcel.class);
    }

}
