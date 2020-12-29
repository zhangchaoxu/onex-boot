package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.sys.dto.AdDTO;
import com.nb6868.onexboot.api.modules.sys.service.AdService;
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
 * 广告位
 * 配置axd是因为ad会被部分广告拦截的浏览器插件拦截掉
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping(value = {"sys/ad", "sys/axd"})
@Validated
@Api(tags="广告位")
public class AdController {

    @Autowired
    AdService adService;

    @DataFilter(tableAlias = "sys_ad", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:ad:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<AdDTO> list = adService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "sys_ad", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:ad:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<AdDTO> page = adService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:ad:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        AdDTO data = adService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:ad:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody AdDTO dto) {
        adService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:ad:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody AdDTO dto) {
        adService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:ad:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        adService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:ad:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        adService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
