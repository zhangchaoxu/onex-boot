package com.nb6868.onexboot.api.modules.log.controller;

import com.nb6868.onexboot.api.common.annotation.AccessControl;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.log.dto.ReleaseDTO;
import com.nb6868.onexboot.api.modules.log.excel.ReleaseExcel;
import com.nb6868.onexboot.api.modules.log.service.ReleaseService;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 更新日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("log/release")
@Validated
@Api(tags = "更新日志")
public class ReleaseController {
    @Autowired
    private ReleaseService releaseService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("log:release:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ReleaseDTO> list = releaseService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("log:release:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ReleaseDTO> page = releaseService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("getLatestByCode")
    @ApiOperation("通过code获取最新的release")
    @AccessControl
    public Result<?> getLatestByCode(@NotBlank(message = "{code.require}") @RequestParam String code) {
        ReleaseDTO data = releaseService.getLatestByCode(code);

        return new Result<ReleaseDTO>().success(data);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("log:release:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ReleaseDTO data = releaseService.getDtoById(id);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("log:release:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ReleaseDTO dto) {
        releaseService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("log:release:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ReleaseDTO dto) {
        releaseService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("log:release:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        releaseService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("log:release:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        releaseService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("log:release:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ReleaseDTO> list = releaseService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "更新日志", list, ReleaseExcel.class);
    }

}
