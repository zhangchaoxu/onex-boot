package com.nb6868.onex.sys.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.DictDTO;
import com.nb6868.onex.sys.dto.DictQueryReq;
import com.nb6868.onex.sys.dto.DictSaveOrUpdateReq;
import com.nb6868.onex.sys.entity.DictEntity;
import com.nb6868.onex.sys.service.DictService;
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

@RestController("SysDict")
@RequestMapping("/sys/dict/")
@Validated
@Tag(name = "数据字典")
public class DictController {

    @Autowired
    DictService dictService;

    @PostMapping("page")
    @Operation(summary = "字典分类")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<PageData<DictDTO>> page(@Validated(PageGroup.class) @RequestBody DictQueryReq req) {
        PageData<DictDTO> page = dictService.pageDto(req, QueryWrapperHelper.getPredicate(req, "page"));

        return new Result<PageData<DictDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "字典分类数据")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<List<DictDTO>> list(@Validated @RequestBody DictQueryReq req) {
        List<DictDTO> list = dictService.listDto(QueryWrapperHelper.getPredicate(req, "list"));

        return new Result<List<DictDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:query"}, logical = Logical.OR)
    public Result<DictDTO> info(@Validated @RequestBody IdReq form) {
        DictDTO data = dictService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<DictDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:edit"}, logical = Logical.OR)
    public Result<DictDTO> saveOrUpdate(@Validated @RequestBody DictSaveOrUpdateReq req) {
        DictEntity entity = dictService.saveOrUpdateByReq(req);
        DictDTO dto = ConvertUtils.sourceToTarget(entity, DictDTO.class);
        return new Result<DictDTO>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(dictService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        dictService.removeById(req.getId());
        return new Result<>();
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:dict", "sys:dict:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsReq req) {
        // 删除数据
        dictService.removeByIds(req.getIds());
        return new Result<>();
    }

}
