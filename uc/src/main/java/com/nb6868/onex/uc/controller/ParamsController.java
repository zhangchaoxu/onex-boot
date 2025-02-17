package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.ParamsDTO;
import com.nb6868.onex.uc.dto.ParamsInfoQueryReq;
import com.nb6868.onex.uc.dto.ParamsQueryReq;
import com.nb6868.onex.uc.dto.ParamsSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.service.ParamsService;
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

@RestController("UcParams")
@RequestMapping("/uc/params/")
@Validated
@Tag(name = "参数管理")
public class ParamsController {

    @Autowired
    private ParamsService paramsService;

    /**
     * 也可以从@RequestHeader(value = "Origin") String originHeader获得
     */
    @PostMapping("infoByCode")
    @AccessControl
    @Operation(summary = "通过编码获取配置信息")
    public Result<?> infoByCode(@Validated @RequestBody ParamsInfoQueryReq form) {
        QueryWrapper<ParamsEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        ParamsEntity data = paramsService.getOne(queryWrapper);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(UcConst.ParamsScopeEnum.PUBLIC.getCode().equalsIgnoreCase(data.getScope()), "参数非公开");
        return new Result<>().success(JacksonUtils.jsonToNode(data.getContent()));
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<List<ParamsDTO>> list(@RequestBody ParamsQueryReq form) {
        List<ParamsDTO> list = paramsService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<List<ParamsDTO>>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PageData<ParamsDTO>> page(@Validated({PageGroup.class}) @RequestBody ParamsQueryReq form) {
        PageData<ParamsDTO> page = paramsService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<ParamsDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<ParamsDTO> info(@Validated @RequestBody IdReq form) {
        ParamsDTO data = paramsService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<ParamsDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:edit"}, logical = Logical.OR)
    public Result<ParamsDTO> saveOrUpdate(@Validated @RequestBody ParamsSaveOrUpdateReq req) {
        ParamsEntity entity = paramsService.saveOrUpdateByReq(req);
        ParamsDTO dto = ConvertUtils.sourceToTarget(entity, ParamsDTO.class);

        return new Result<ParamsDTO>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:params:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(paramsService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        paramsService.removeById(req.getId());
        return new Result<>();
    }

}
