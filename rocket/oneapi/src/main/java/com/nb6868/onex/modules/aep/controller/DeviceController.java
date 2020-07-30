package com.nb6868.onex.modules.aep.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.aep.dto.DeviceDTO;
import com.nb6868.onex.modules.aep.entity.DeviceEntity;
import com.nb6868.onex.modules.aep.entity.ProductEntity;
import com.nb6868.onex.modules.aep.excel.DeviceExcel;
import com.nb6868.onex.modules.aep.service.DeviceService;
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
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Device")
@RequestMapping("aep/device")
@Validated
@Api(tags = "AEP-设备")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ProductService productService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("aep:device:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<DeviceDTO> list = deviceService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("aep:device:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<DeviceDTO> page = deviceService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("aep:device:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        DeviceDTO data = deviceService.getDtoById(id);

        return new Result<DeviceDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("aep:device:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DeviceDTO dto) {
        boolean ret = deviceService.saveDto(dto);
        if (ret) {
            return new Result<>().success(dto);
        } else {
            return new Result<>().error("添加失败");
        }
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("aep:device:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DeviceDTO dto) {
        boolean ret =  deviceService.updateDto(dto);

        if (ret) {
            return new Result<>().success(dto);
        } else {
            return new Result<>().error("修改失败");
        }
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("aep:device:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        DeviceEntity data = deviceService.getById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        ProductEntity product = productService.getByProductId(data.getProductId());
        AssertUtils.isNull(product, "产品信息为空");

        boolean ctWingRet = deviceService.deleteFromCtwingByDeviceIds(data.getProductId(), product.getApiKey(), String.valueOf(id));
        if (!ctWingRet) {
            return new Result<>().error("删除平台信息失败");
        } else {
            deviceService.logicDeleteById(id);
            return new Result<>();
        }
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("aep:device:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        for (Long id : ids) {
            DeviceEntity data = deviceService.getById(id);
            AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

            ProductEntity product = productService.getByProductId(data.getProductId());
            AssertUtils.isNull(product, "产品信息为空");

            boolean ctWingRet = deviceService.deleteFromCtwingByDeviceIds(data.getProductId(), product.getApiKey(), data.getDeviceId());
            if (ctWingRet) {
                deviceService.logicDeleteById(id);
            }
        }
        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("aep:device:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<DeviceDTO> list = deviceService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "AEP-设备", list, DeviceExcel.class);
    }

    @PostMapping("sync")
    @ApiOperation("同步")
    @LogOperation("同步")
    @RequiresPermissions("aep:device:sync")
    public Result<?> sync(@RequestParam(required = false) String searchValue) {
        boolean ret = deviceService.sync(searchValue);
        return new Result<>().setCode(ret ? ErrorCode.SUCCESS : ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("sendCommand")
    @ApiOperation("下发指令")
    @LogOperation("下发指令")
    @RequiresPermissions("aep:device:sendCommand")
    public Result<?> sendCommand(@RequestParam String deviceId, @RequestParam String command) {
        boolean ret = deviceService.sendCommand(deviceId, command);
        return new Result<>().setCode(ret ? ErrorCode.SUCCESS : ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
