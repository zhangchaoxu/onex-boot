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
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.DeptDTO;
import com.nb6868.onex.uc.dto.DeptQueryForm;
import com.nb6868.onex.uc.entity.DeptEntity;
import com.nb6868.onex.uc.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/dept")
@Validated
@Api(tags = "部门管理", position = 40)
public class DeptController {

    @Autowired
    private DeptService deptService;

    @PostMapping("tree")
    @ApiOperation("树表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("uc:dept:query")
    public Result<?> tree(@Validated @RequestBody DeptQueryForm form) {
        QueryWrapper<DeptEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<Tree<String>> treeList = TreeNodeUtils.buildCodeTree(
                CollStreamUtil.toList(deptService.list(queryWrapper),
                        (entity) -> new TreeNode<>(entity.getCode(), entity.getPcode(), entity.getName(), entity.getSort()).setExtra(Dict.create().set("type", entity.getType()))));
        return new Result<>().success(treeList);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:dept:query")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<DeptDTO> list = deptService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:dept:query")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<DeptDTO> page = deptService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:dept:query")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        DeptDTO data = deptService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:dept:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody DeptDTO dto) {
        deptService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:dept:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody DeptDTO dto) {
        deptService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:dept:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        deptService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:dept:delete")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        deptService.logicDeleteByIds(ids);

        return new Result<>();
    }

}
