package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.sys.dto.DictDTO;
import com.nb6868.onexboot.api.modules.sys.service.DictService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 数据字典
 */
@RestController
@RequestMapping("sys/dict")
@Validated
@Api(tags="数据字典")
public class DictController {
    @Autowired
    private DictService dictService;

    @GetMapping("page")
    @ApiOperation("字典分类")
    @RequiresPermissions("sys:dict:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<DictDTO> page = dictService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("list")
    @ApiOperation("字典分类数据")
    @RequiresPermissions("sys:dict:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params){
        List<DictDTO> list = dictService.listDto(params);

        return new Result<List<DictDTO>>().success(list);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:dict:info")
    public Result<DictDTO> info(@NotNull(message = "{id.require}") @RequestParam Long id){
        DictDTO data = dictService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<DictDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:dict:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DictDTO dto){
        dictService.saveDto(dto);

        return new Result<>();
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:dict:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DictDTO dto){
        dictService.updateDto(dto);

        return new Result<>();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:dict:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id){
        dictService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:dict:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids){
        dictService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
