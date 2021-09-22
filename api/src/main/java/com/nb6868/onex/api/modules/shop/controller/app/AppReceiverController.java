package com.nb6868.onex.api.modules.shop.controller.app;

import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.modules.shop.dto.AppReceiverRequest;
import com.nb6868.onex.api.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.api.modules.shop.service.ReceiverService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 收货地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("app/shop/receiver")
@Validated
@Api(tags="收货地址")
public class AppReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @DataSqlScope(tableAlias = "shop_receiver", userFilter = true, userId = "user_id")
    @GetMapping("list")
    @ApiOperation("列表")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<ReceiverDTO> list = receiverService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ReceiverDTO data = receiverService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody AppReceiverRequest dto) {
        return new Result<>().success(receiverService.saveAppReceiver(dto));
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody AppReceiverRequest dto) {
        return new Result<>().success(receiverService.updateAppReceiver(dto));
    }

    @PutMapping("setDefaultItem")
    @ApiOperation("设置默认")
    @LogOperation("设置默认")
    public Result<?> setDefaultItem(@NotNull(message = "{id.require}") @RequestParam Long id) {
        receiverService.setDefaultItem(id);

        return new Result<>();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        receiverService.logicDeleteById(id);

        return new Result<>();
    }
}
