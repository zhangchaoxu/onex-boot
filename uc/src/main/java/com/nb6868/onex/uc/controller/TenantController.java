package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.TenantDTO;
import com.nb6868.onex.uc.dto.TenantQueryReq;
import com.nb6868.onex.uc.dto.TenantSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.TenantEntity;
import com.nb6868.onex.uc.service.TenantService;
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

@RestController("UcTenant")
@RequestMapping("/uc/tenant/")
@Validated
@Tag(name = "租户管理")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    public Result<PageData<TenantDTO>> page(@Validated({PageGroup.class}) @RequestBody TenantQueryReq form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<TenantDTO> page = tenantService.pageDto(form, queryWrapper);

        return new Result<PageData<TenantDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    public Result<List<TenantDTO>> list(@Validated @RequestBody TenantQueryReq form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<TenantDTO> list = tenantService.listDto(queryWrapper);

        return new Result<List<TenantDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    public Result<TenantDTO> info(@Validated @RequestBody IdReq form) {
        TenantDTO data = tenantService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<TenantDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:edit"}, logical = Logical.OR)
    public Result<TenantDTO> saveOrUpdate(@Validated @RequestBody TenantSaveOrUpdateReq req) {
        TenantEntity entity = tenantService.saveOrUpdateByReq(req);
        TenantDTO dto = ConvertUtils.sourceToTarget(entity, TenantDTO.class);

        return new Result<TenantDTO>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(tenantService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        tenantService.removeById(req.getId());
        // todo 按业务需求做其它操作
        return new Result<>();
    }

}
