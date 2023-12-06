package com.nb6868.onex.sys.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.sys.dto.DictDTO;
import com.nb6868.onex.sys.dto.DictQueryForm;
import com.nb6868.onex.sys.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/dict")
@Validated
@Tag(name = "数据字典")
public class DictController {

    @Autowired
    private DictService dictService;

    @PostMapping("page")
    @Operation(summary = "字典分类")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<?> page(@Validated(PageGroup.class) @RequestBody DictQueryForm form) {
        PageData<?> page = dictService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "字典分类数据")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<?> list(@Validated @RequestBody DictQueryForm form) {
        List<?> list = dictService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<DictDTO> info(@Validated @RequestBody IdForm form) {
        DictDTO data = dictService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<DictDTO>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:edit"}, logical = Logical.OR)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DictDTO dto) {
        dictService.saveDto(dto);

        return new Result<>();
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:edit"}, logical = Logical.OR)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DictDTO dto) {
        dictService.updateDto(dto);

        return new Result<>();
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        dictService.removeById(form.getId());

        return new Result<>();
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        dictService.removeByIds(form.getIds());

        return new Result<>();
    }

}
