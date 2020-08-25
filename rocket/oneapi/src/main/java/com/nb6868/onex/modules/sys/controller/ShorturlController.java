package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.sys.dto.ShorturlDTO;
import com.nb6868.onex.modules.sys.excel.ShorturlExcel;
import com.nb6868.onex.modules.sys.service.ShorturlService;
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
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("sys/shorturl")
@Validated
@Api(tags = "短地址")
public class ShorturlController {
    @Autowired
    private ShorturlService shorturlService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:shorturl:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ShorturlDTO> list = shorturlService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:shorturl:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ShorturlDTO> page = shorturlService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:shorturl:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        ShorturlDTO data = shorturlService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<ShorturlDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:shorturl:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ShorturlDTO dto) {
        shorturlService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:shorturl:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ShorturlDTO dto) {
        shorturlService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:shorturl:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        shorturlService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:shorturl:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        shorturlService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:shorturl:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ShorturlDTO> list = shorturlService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "短地址", list, ShorturlExcel.class);
    }

}
