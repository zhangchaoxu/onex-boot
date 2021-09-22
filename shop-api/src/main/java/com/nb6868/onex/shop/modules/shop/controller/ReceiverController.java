package com.nb6868.onex.shop.modules.shop.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.shop.modules.shop.dto.ReceiverDTO;
import com.nb6868.onex.shop.modules.shop.entity.ReceiverEntity;
import com.nb6868.onex.shop.modules.shop.service.ReceiverService;
import com.nb6868.onex.shop.shiro.SecurityUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController("ShopReceiverController")
@RequestMapping("/shop/receiver")
@Validated
@Slf4j
@Api(tags="收货地址", position = 90)
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @GetMapping("list")
    @ApiOperation(value = "列表", position = 10)
    public Result<?> list() {
        // 按条件获得列表
        List<ReceiverEntity> entityList = receiverService.query()
                .eq("user_id", SecurityUser.getUserId())
                .orderByDesc("create_time")
                .list();
        // 转成dto
        List<ReceiverDTO> dtoList = ConvertUtils.sourceToTarget(entityList, ReceiverDTO.class);
        return new Result<>().success(dtoList);
    }

    @GetMapping("info")
    @ApiOperation(value = "信息", position = 20)
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ReceiverEntity entity = receiverService.getByIdAndUserId(id, SecurityUser.getUserId());
        AssertUtils.isNull(entity, ErrorCode.DB_RECORD_EXISTS);
        // 转成dto
        ReceiverDTO dto = ConvertUtils.sourceToTarget(entity, ReceiverDTO.class);

        return new Result<>().success(dto);
    }

    @PostMapping("save")
    @ApiOperation(value = "保存", position = 30)
    @LogOperation("保存")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ReceiverDTO dto) {
        // todo 注意. 检查内容以及默认项
        return new Result<>().success();
    }

    @PutMapping("update")
    @ApiOperation(value = "修改", position = 40)
    @LogOperation("修改")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ReceiverDTO dto) {
        // todo 注意. 检查内容以及默认项
        return new Result<>().success();
    }

    @PutMapping("setDefaultItem")
    @ApiOperation(value = "设为默认地址", position = 50)
    @LogOperation("设为默认地址")
    public Result<?> setDefaultItem(@NotNull(message = "{id.require}") @RequestParam Long id) {
        Long userId = SecurityUser.getUserId();
        // 按条件获得数据
        ReceiverEntity entity = receiverService.getByIdAndUserId(id, userId);
        AssertUtils.isEmpty(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 将该会员其它地址设置为非默认
        receiverService.update().eq("user_id", userId).set("default_item", 0).ne("id", id).update(new ReceiverEntity());
        // 将该地址设置为默认项
        receiverService.update().eq("id", id).set("default_item", 1).update(new ReceiverEntity());
        return new Result<>();
    }

    @DeleteMapping("delete")
    @ApiOperation(value = "删除", position = 100)
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        Long userId = SecurityUser.getUserId();
        // 按条件获得数据
        ReceiverEntity entity = receiverService.getByIdAndUserId(id, userId);
        AssertUtils.isEmpty(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
        if (entity.getDefaultItem() == Const.BooleanEnum.TRUE.value()) {
            // 删除了一个默认项,需要将另外一个设置为默认项
            // 将该会员最新一条地址设置为非默认
            receiverService.update()
                    .set("default_item", 1)
                    .eq("user_id", userId)
                    .ne("id", id)
                    .orderByDesc("create_time")
                    .last(Const.LIMIT_ONE)
                    .update(new ReceiverEntity());
        }
        receiverService.logicDeleteById(id);

        return new Result<>();
    }


}
