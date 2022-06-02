package com.nb6868.onex.sys.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.sys.dto.RegionDTO;
import com.nb6868.onex.sys.dto.RegionQueryForm;
import com.nb6868.onex.sys.entity.RegionEntity;
import com.nb6868.onex.sys.service.RegionService;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/sys/region")
@Validated
@Api(tags = "行政区域", position = 100)
public class RegionController {
    @Autowired
    private RegionService regionService;

    @PostMapping("tree")
    @ApiOperation("树表")
    @RequiresPermissions("sys:region:query")
    public Result<?> tree(@Validated @RequestBody RegionQueryForm form) {
        QueryWrapper<RegionEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<TreeNode<Long>> nodeList = new ArrayList<>();
        regionService.list(queryWrapper).forEach(entity -> nodeList.add(new TreeNode<>(entity.getId(), entity.getPid(), entity.getName(), entity.getId())
                .setExtra(Dict.create().set("extName", entity.getExtName()))));
        List<Tree<Long>> treeList = TreeNodeUtils.buildIdTree(nodeList);
        return new Result<>().success(treeList);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("sys:region:query")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<RegionDTO> list = regionService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:region:query")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<RegionDTO> page = regionService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:region:query")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        RegionDTO data = regionService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:region:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RegionDTO dto) {
        regionService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:region:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody RegionDTO dto) {
        regionService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:region:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        regionService.deleteById(id);

        return new Result<>();
    }

}
