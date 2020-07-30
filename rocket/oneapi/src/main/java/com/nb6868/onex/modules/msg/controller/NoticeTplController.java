package com.nb6868.onex.modules.msg.controller;

import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.msg.dto.NoticeTplDTO;
import com.nb6868.onex.modules.msg.excel.NoticeTplExcel;
import com.nb6868.onex.modules.msg.service.NoticeTplService;
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
 * 通知模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("msg/noticeTpl")
@Validated
@Api(tags="通知模板")
public class NoticeTplController {
    @Autowired
    private NoticeTplService noticeTplService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("msg:noticeTpl:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params){
        List<NoticeTplDTO> list = noticeTplService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("msg:noticeTpl:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<NoticeTplDTO> page = noticeTplService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("msg:noticeTpl:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id){
        NoticeTplDTO data = noticeTplService.getDtoById(id);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("msg:noticeTpl:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody NoticeTplDTO dto){
        noticeTplService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("msg:noticeTpl:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody NoticeTplDTO dto){
        noticeTplService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("msg:noticeTpl:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id){
        noticeTplService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("msg:noticeTpl:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids){
        noticeTplService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("msg:noticeTpl:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<NoticeTplDTO> list = noticeTplService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "通知模板", list, NoticeTplExcel.class);
    }

}
