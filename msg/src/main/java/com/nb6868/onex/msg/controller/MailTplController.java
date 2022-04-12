package com.nb6868.onex.msg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdTenantForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.msg.dto.MailTplDTO;
import com.nb6868.onex.msg.dto.MailTplQueryForm;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.service.MailTplService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/msg/mailTpl")
@Validated
@Api(tags = "消息模板", position = 10)
@ApiSupport(order = 10)
public class MailTplController {

    @Autowired
    MailTplService mailTplService;

    @PostMapping("list")
    @ApiOperation("列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 10)
    public Result<?> list(@Validated @RequestBody MailTplQueryForm form) {
        QueryWrapper<MailTplEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<?> list = mailTplService.listDto(queryWrapper);
        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 20)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody MailTplQueryForm form) {
        QueryWrapper<MailTplEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        PageData<MailTplDTO> page = mailTplService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:query")
    @ApiOperationSupport(order = 30)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        MailTplDTO data = mailTplService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("msg:mailTpl:delete")
    @ApiOperationSupport(order = 60)
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        mailTplService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("msg:mailTpl:edit")
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MailTplDTO dto) {
        mailTplService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("msg:mailTpl:edit")
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MailTplDTO dto) {
        mailTplService.updateDto(dto);

        return new Result<>().success(dto);
    }

}
