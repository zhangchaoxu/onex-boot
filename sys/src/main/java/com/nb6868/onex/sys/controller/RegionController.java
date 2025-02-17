package com.nb6868.onex.sys.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.TreeNodeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.RegionDTO;
import com.nb6868.onex.sys.dto.RegionQueryReq;
import com.nb6868.onex.sys.dto.RegionSaveOrUpdateReq;
import com.nb6868.onex.sys.entity.RegionEntity;
import com.nb6868.onex.sys.service.RegionService;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("SysRegion")
@RequestMapping("/sys/region/")
@Validated
@Tag(name = "行政区域")
public class RegionController {

    @Autowired
    RegionService regionService;

    @PostMapping("tree")
    @Operation(summary = "树表")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:query"}, logical = Logical.OR)
    public Result<List<Tree<Long>>> tree(@Validated @RequestBody RegionQueryReq form) {
        QueryWrapper<RegionEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<TreeNode<Long>> nodeList = new ArrayList<>();
        regionService.list(queryWrapper).forEach(entity -> nodeList.add(new TreeNode<>(entity.getId(), entity.getPid(), entity.getName(), entity.getId())
                .setExtra(Dict.create().set("extName", entity.getExtName()))));
        List<Tree<Long>> treeList = TreeNodeUtils.buildIdTree(nodeList);
        return new Result<List<Tree<Long>>>().success(treeList);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:query"}, logical = Logical.OR)
    public Result<List<RegionDTO>> list(@Validated @RequestBody RegionQueryReq form) {
        List<RegionDTO> list = regionService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<List<RegionDTO>>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:query"}, logical = Logical.OR)
    public Result<PageData<RegionDTO>> page(@Validated(PageGroup.class) @RequestBody RegionQueryReq form) {
        PageData<RegionDTO> page = regionService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<RegionDTO>>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:query"}, logical = Logical.OR)
    public Result<RegionDTO> info(@Validated @RequestBody IdReq form) {
        RegionDTO data = regionService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<RegionDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:edit"}, logical = Logical.OR)
    public Result<RegionDTO> saveOrUpdate(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RegionSaveOrUpdateReq req) {
        RegionEntity entity = regionService.saveOrUpdateByReq(req);
        RegionDTO dto = ConvertUtils.sourceToTarget(entity, RegionDTO.class);

        return new Result<RegionDTO>().success(dto);
    }


    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:region", "sys:region:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(regionService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        regionService.removeById(req.getId());
        // 删除子数据
        regionService.remove(regionService.lambdaQuery().likeRight(RegionEntity::getId, req.getId()).getWrapper());
        return new Result<>();
    }

}
