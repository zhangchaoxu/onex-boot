package com.nb6868.onex.modules.aep.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.aep.dto.SubscriptionPushMessageDTO;
import com.nb6868.onex.modules.aep.excel.SubscriptionPushMessageExcel;
import com.nb6868.onex.modules.aep.service.SubscriptionPushMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * AEP-订阅消息通知
 * see {https://help.ctwing.cn/ding-yue-tui-song/tui-song-xiao-xi-ge-shi.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("SubscriptionPushMessage")
@RequestMapping("aep/subscriptionPushMessage")
@Validated
@Api(tags = "AEP-订阅消息通知")
public class SubscriptionPushMessageController {
    @Autowired
    private SubscriptionPushMessageService subscriptionPushMessageService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("aep:subscriptionPushMessage:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<SubscriptionPushMessageDTO> page = subscriptionPushMessageService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("aep:subscriptionPushMessage:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        SubscriptionPushMessageDTO data = subscriptionPushMessageService.getDtoById(id);

        return new Result<SubscriptionPushMessageDTO>().success(data);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("aep:subscriptionPushMessage:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        subscriptionPushMessageService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("aep:subscriptionPushMessage:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}")@RequestBody List<Long> ids) {
        subscriptionPushMessageService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("aep:subscriptionPushMessage:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<SubscriptionPushMessageDTO> list = subscriptionPushMessageService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "AEP-订阅消息通知", list, SubscriptionPushMessageExcel.class);
    }

    @PostMapping("notify")
    @ApiOperation("订阅通知")
    @AnonAccess
    public Result<?> notify(@RequestBody SubscriptionPushMessageDTO message) {
        boolean ret = subscriptionPushMessageService.notify(message);
        if (ret) {
            return new Result<>();
        } else {
            return new Result<>().error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
