package com.nb6868.onexboot.api.modules.uc.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.uc.dto.UserOauthDTO;
import com.nb6868.onexboot.api.modules.uc.service.UserOauthService;
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
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("UserOauth")
@RequestMapping("/uc/userOauth")
@Validated
@Api(tags = "第三方用户")
public class UserOauthController {

    @Autowired
    UserOauthService userOauthService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:userOauth:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserOauthDTO> list = userOauthService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:userOauth:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<UserOauthDTO> page = userOauthService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:userOauth:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        UserOauthDTO data = userOauthService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<UserOauthDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:userOauth:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:userOauth:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:userOauth:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        userOauthService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:userOauth:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userOauthService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
