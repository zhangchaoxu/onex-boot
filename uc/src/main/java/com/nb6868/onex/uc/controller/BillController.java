package com.nb6868.onex.uc.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.BillDTO;
import com.nb6868.onex.uc.dto.BillQueryForm;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.dto.UserQueryForm;
import com.nb6868.onex.uc.entity.BillEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController("ucBill")
@RequestMapping("/uc/bill/")
@Validated
@Api(tags = "用户中心-账单流水", position = 100)
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:bill:list")
    public Result<?> list(@Validated @RequestBody BillQueryForm form) {
        QueryWrapper<BillEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<?> list = billService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:bill:page")
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody BillQueryForm form) {
        QueryWrapper<BillEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");

        PageData<?> page = billService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:bill:info")
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        BillDTO data = billService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:bill:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BillDTO dto) {
        billService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:bill:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BillDTO dto) {
        billService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:bill:delete")
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        billService.logicDeleteByWrapper(QueryWrapperHelper.getPredicate(form));

        return new Result<>();
    }

}
