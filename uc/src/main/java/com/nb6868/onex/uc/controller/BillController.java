package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.BillDTO;
import com.nb6868.onex.uc.dto.BillQueryForm;
import com.nb6868.onex.uc.entity.BillEntity;
import com.nb6868.onex.uc.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UcBill")
@RequestMapping("/uc/bill/")
@Validated
@Tag(name = "账单流水")
public class BillController {

    @Autowired
    BillService billService;

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions("uc:bill:list")
    public Result<List<BillDTO>> list(@Validated @RequestBody BillQueryForm form) {
        QueryWrapper<BillEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<BillDTO> list = billService.listDto(queryWrapper);

        return new Result<List<BillDTO>>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions("uc:bill:page")
    public Result<PageData<BillDTO>> page(@Validated({PageGroup.class}) @RequestBody BillQueryForm form) {
        QueryWrapper<BillEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");

        PageData<BillDTO> page = billService.pageDto(form, queryWrapper);

        return new Result<PageData<BillDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions("uc:bill:info")
    public Result<BillDTO> info(@Validated @RequestBody IdReq form) {
        BillDTO data = billService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<BillDTO>().success(data);
    }

    /*@PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:bill:save")
    public Result<BillDTO> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody BillDTO dto) {
        billService.saveDto(dto);

        return new Result<BillDTO>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:bill:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody BillDTO dto) {
        billService.updateDto(dto);

        return new Result<>().success(dto);
    }*/

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:bill:delete")
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(billService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        billService.removeById(req.getId());
        return new Result<>();
    }

}
