package com.nb6868.onex.modules.ba.controller;

import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.ba.dto.ExamUserDTO;
import com.nb6868.onex.modules.ba.service.ExamUserService;
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
 * 秉奥-用户检测
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("ba/examUser")
@Validated
@Api(tags="秉奥-用户检测")
public class ExamUserController {
    @Autowired
    private ExamUserService examUserService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("ba:examUser:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ExamUserDTO> list = examUserService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("ba:examUser:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ExamUserDTO> page = examUserService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("ba:examUser:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ExamUserDTO data = examUserService.getDtoById(id);

        return new Result<ExamUserDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("ba:examUser:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ExamUserDTO dto) {
        examUserService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("ba:examUser:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ExamUserDTO dto) {
        examUserService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("ba:examUser:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        examUserService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("ba:examUser:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        examUserService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
