package com.nb6868.onex.api.modules.shop.controller;

import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.shop.dto.UserRankDTO;
import com.nb6868.onex.api.modules.shop.service.UserRankService;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 用户等级
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("shop/userRank")
@Validated
@Api(tags="用户等级")
public class UserRankController {
    @Autowired
    private UserRankService userrankService;

    @DataFilter(tableAlias = "shop_user_rank", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("shop:userRank:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserRankDTO> list = userrankService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "shop_user_rank", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("shop:userRank:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<UserRankDTO> page = userrankService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("shop:userRank:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        UserRankDTO data = userrankService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("shop:userRank:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserRankDTO dto) {
        userrankService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("shop:userRank:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserRankDTO dto) {
        userrankService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("shop:userRank:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        userrankService.logicDeleteById(id);

        return new Result<>();
    }

}
