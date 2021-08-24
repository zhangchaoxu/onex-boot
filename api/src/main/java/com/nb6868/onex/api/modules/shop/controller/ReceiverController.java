package com.nb6868.onex.api.modules.shop.controller;

import com.nb6868.onex.api.common.annotation.DataFilter;
import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.api.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.api.modules.shop.excel.ReceiverExcel;
import com.nb6868.onex.api.modules.shop.service.ReceiverService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.AdminAddForUserGroup;
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
 * 收件地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/shop/receiver")
@Validated
@Api(tags = "收件地址")
public class ReceiverController {
    @Autowired
    private ReceiverService receiverService;

    @DataFilter(tableAlias = "shop_receiver", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:receiver:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ReceiverDTO> list = receiverService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_receiver", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:receiver:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ReceiverDTO> page = receiverService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:receiver:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ReceiverDTO data = receiverService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:receiver:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class, AdminAddForUserGroup.class}) @RequestBody ReceiverDTO dto) {
        receiverService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:receiver:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ReceiverDTO dto) {
        receiverService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("setDefaultItem")
    @ApiOperation("设置默认")
    @LogOperation("设置默认")
    @RequiresPermissions("shop:receiver:update")
    public Result<?> setDefaultItem(@NotNull(message = "{id.require}") @RequestParam Long id) {
        receiverService.setDefaultItem(id);

        return new Result<>();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:receiver:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        receiverService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("shop:receiver:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        receiverService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("shop:receiver:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ReceiverDTO> list = receiverService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "收件地址", list, ReceiverExcel.class);
    }

}
