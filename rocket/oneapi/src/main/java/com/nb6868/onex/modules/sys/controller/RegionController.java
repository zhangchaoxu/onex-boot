package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.modules.sys.dto.RegionDTO;
import com.nb6868.onex.modules.sys.dto.RegionTreeDTO;
import com.nb6868.onex.modules.sys.service.RegionService;
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
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("sys/region")
@Validated
@Api(tags="行政区域")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("tree")
    @ApiOperation("树表")
    @RequiresPermissions("sys:region:list")
    public Result<?> tree(@ApiIgnore @RequestParam Map<String, Object> params){
        List<RegionTreeDTO> list = regionService.treeList(params);

        return new Result<>().success(list);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:region:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params){
        List<RegionDTO> list = regionService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:region:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<RegionDTO> page = regionService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:region:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id){
        RegionDTO data = regionService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:region:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RegionDTO dto){
        regionService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:region:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody RegionDTO dto){
        regionService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:region:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id){
        regionService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:region:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids){
        regionService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
