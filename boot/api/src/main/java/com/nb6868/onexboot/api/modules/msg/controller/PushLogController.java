package com.nb6868.onexboot.api.modules.msg.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.msg.dto.PushLogDTO;
import com.nb6868.onexboot.api.modules.msg.dto.PushSendRequest;
import com.nb6868.onexboot.api.modules.msg.push.AbstractPushService;
import com.nb6868.onexboot.api.modules.msg.push.PushFactory;
import com.nb6868.onexboot.api.modules.msg.push.PushProps;
import com.nb6868.onexboot.api.modules.msg.service.PushLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
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
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("msg/pushLog")
@Validated
@Api(tags = "消息推送记录")
public class PushLogController {
    @Autowired
    private PushLogService pushLogService;
    @Autowired
    private ParamService paramService;

    @PostMapping("/send")
    @ApiOperation("发送消息推送")
    @LogOperation("发送消息推送")
    public Result<?> send(@Validated(value = {AddGroup.class}) @RequestBody PushSendRequest dto) {
        PushProps config = paramService.getContentObject(Const.PUSH_CONFIG_KEY, PushProps.class);
        if (config == null) {
            return new Result<>().error("未找到对应的推送配置");
        }

        // 获取推送服务
        AbstractPushService service = PushFactory.build(config);

        // 发送推送
        service.send(config, dto.getPushType(), dto.getAlias(), dto.getTags(), dto.getTitle(), dto.getContent(), dto.getParams(), dto.getApnsProd());

        return new Result<>();
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("msg:pushLog:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<?> list = pushLogService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("msg:pushLog:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<PushLogDTO> page = pushLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("msg:pushLog:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        PushLogDTO data = pushLogService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("msg:pushLog:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody PushLogDTO dto) {
        pushLogService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("msg:pushLog:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody PushLogDTO dto) {
        pushLogService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("msg:pushLog:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        pushLogService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("msg:pushLog:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        pushLogService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
