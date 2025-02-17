package com.nb6868.onex.uc.controller;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.DeptDTO;
import com.nb6868.onex.uc.dto.DeptQueryReq;
import com.nb6868.onex.uc.dto.DeptSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.DeptEntity;
import com.nb6868.onex.uc.service.DeptService;
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

@RestController("UcDept")
@RequestMapping("/uc/dept/")
@Validated
@Tag(name = "部门管理")
public class DeptController {

    @Autowired
    DeptService deptService;

    @PostMapping("tree")
    @Operation(summary = "树表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<List<Tree<String>>> tree(@Validated @RequestBody DeptQueryReq form) {
        QueryWrapper<DeptEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<Tree<String>> treeList = TreeNodeUtils.buildCodeTree(
                CollStreamUtil.toList(deptService.list(queryWrapper),
                        (entity) -> new TreeNode<>(entity.getCode(), entity.getPcode(), entity.getName(), entity.getSort()).setExtra(Dict.create().set("type", entity.getType()))));
        return new Result<List<Tree<String>>>().success(treeList);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<List<DeptDTO>> list(@Validated @RequestBody DeptQueryReq form) {
        List<DeptDTO> list = deptService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<List<DeptDTO>>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<PageData<DeptDTO>> page(@Validated(PageGroup.class) @RequestBody DeptQueryReq form) {
        PageData<DeptDTO> page = deptService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<DeptDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:query"}, logical = Logical.OR)
    public Result<DeptDTO> info(@Validated @RequestBody IdReq form) {
        DeptDTO data = deptService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<DeptDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:edit"}, logical = Logical.OR)
    public Result<DeptDTO> saveOrUpdate(@Validated @RequestBody DeptSaveOrUpdateReq req) {
        DeptEntity entity = deptService.saveOrUpdateByReq(req);
        DeptDTO dto = ConvertUtils.sourceToTarget(entity, DeptDTO.class);

        return new Result<DeptDTO>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:dept:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(deptService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        deptService.removeById(req.getId());
        // todo 级联删除子部门
        return new Result<>();
    }

}
