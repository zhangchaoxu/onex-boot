package com.nb6868.onex.msg.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.msg.MsgSendReq;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.msg.dto.*;
import com.nb6868.onex.msg.entity.MsgTplEntity;
import com.nb6868.onex.msg.service.MsgLogService;
import com.nb6868.onex.msg.service.MsgService;
import com.nb6868.onex.msg.service.MsgTplService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("SysMsg")
@RequestMapping("/sys/msg/")
@Validated
@Tag(name = "消息管理")
public class MsgController {

    @Autowired
    MsgService msgService;
    @Autowired
    MsgTplService msgTplService;
    @Autowired
    MsgLogService msgLogService;

    @PostMapping("tplPage")
    @Operation(summary = "模板分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    public Result<PageData<MsgTplDTO>> tplPage(@Validated({PageGroup.class}) @RequestBody MsgTplQueryReq req) {
        PageData<MsgTplDTO> page = msgTplService.pageDto(req, QueryWrapperHelper.getPredicate(req, "page"));

        return new Result<PageData<MsgTplDTO>>().success(page);
    }

    @PostMapping("tplList")
    @Operation(summary = "模板列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    public Result<List<MsgTplDTO>> tplList(@Validated @RequestBody MsgTplQueryReq req) {
        List<MsgTplDTO> list = msgTplService.listDto(QueryWrapperHelper.getPredicate(req));
        return new Result<List<MsgTplDTO>>().success(list);
    }

    @PostMapping("tplInfo")
    @Operation(summary = "模板详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    public Result<MsgTplDTO> info(@Validated @RequestBody IdReq req) {
        MsgTplDTO data = msgTplService.oneDto(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<MsgTplDTO>().success(data);
    }

    @PostMapping("tplSaveOrUpdate")
    @Operation(summary = "模板新增或保存")
    @LogOperation("模板新增或保存")
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:edit"}, logical = Logical.OR)
    public Result<MsgTplDTO> tplSaveOrUpdate(@Validated @RequestBody MsgTplSaveOrUpdateReq req) {
        MsgTplEntity entity = msgTplService.saveOrUpdateByReq(req);
        MsgTplDTO dto = ConvertUtils.sourceToTarget(entity, MsgTplDTO.class);

        return new Result<MsgTplDTO>().success(dto);
    }

    @PostMapping("tplDelete")
    @Operation(summary = "模板删除")
    @LogOperation("模板删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(msgTplService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        msgTplService.removeById(req.getId());
        return new Result<>();
    }

    @PostMapping("logPage")
    @Operation(summary = "日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgLog:query"}, logical = Logical.OR)
    public Result<PageData<MsgLogDTO>> page(@Validated({PageGroup.class}) @RequestBody MsgLogQueryReq req) {
        PageData<MsgLogDTO> page = msgLogService.pageDto(req, QueryWrapperHelper.getPredicate(req, "page"));

        return new Result<PageData<MsgLogDTO>>().success(page);
    }

    @PostMapping("send")
    @Operation(summary = "发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msg:send"}, logical = Logical.OR)
    public Result<?> send(@Validated(value = {DefaultGroup.class}) @RequestBody MsgSendReq req) {
        boolean flag = msgService.sendMail(req);
        return new Result<>().bool(flag);
    }

    @PostMapping("logDeleteBatch")
    @Operation(summary = "记录批量删除")
    @LogOperation("记录批量删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgLog:delete"}, logical = Logical.OR)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsReq req) {
        // 删除数据
        msgLogService.removeByIds(req.getIds());
        return new Result<>();
    }

}
