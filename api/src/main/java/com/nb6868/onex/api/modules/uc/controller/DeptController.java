package com.nb6868.onex.api.modules.uc.controller;

import com.nb6868.onex.api.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.uc.dto.DeptDTO;
import com.nb6868.onex.api.modules.uc.dto.DeptTreeDTO;
import com.nb6868.onex.api.modules.uc.service.DeptService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/dept")
@Validated
@Api(tags = "部门管理")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("tree")
    @ApiOperation("树表")
    @RequiresPermissions("uc:dept:list")
    public Result<?> tree(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<DeptTreeDTO> list = deptService.treeList(params);

        return new Result<>().success(list);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:dept:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<DeptDTO> list = deptService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:dept:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<DeptDTO> page = deptService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:dept:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        DeptDTO data = deptService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:dept:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DeptDTO dto) {
        deptService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:dept:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DeptDTO dto) {
        deptService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:dept:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        deptService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:dept:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        deptService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
