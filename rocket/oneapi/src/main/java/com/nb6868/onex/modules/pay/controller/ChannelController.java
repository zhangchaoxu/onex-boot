package com.nb6868.onex.modules.pay.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.pay.dto.ChannelDTO;
import com.nb6868.onex.modules.pay.excel.ChannelExcel;
import com.nb6868.onex.modules.pay.service.ChannelService;
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
 * 支付渠道
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Channel")
@RequestMapping("pay/channel")
@Validated
@Api(tags = "支付渠道")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("pay:channel:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ChannelDTO> list = channelService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("pay:channel:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ChannelDTO> page = channelService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("pay:channel:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ChannelDTO data = channelService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<ChannelDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("pay:channel:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ChannelDTO dto) {
        channelService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("pay:channel:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ChannelDTO dto) {
        channelService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("pay:channel:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        channelService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("pay:channel:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        channelService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("pay:channel:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ChannelDTO> list = channelService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "支付渠道", list, ChannelExcel.class);
    }

}
