package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.PostDTO;
import com.nb6868.onex.uc.dto.PostQueryReq;
import com.nb6868.onex.uc.dto.PostSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.PostEntity;
import com.nb6868.onex.uc.service.PostService;
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

@RestController("UcPost")
@RequestMapping("/uc/post/")
@Validated
@Tag(name = "岗位管理")
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PageData<PostDTO>> page(@Validated({PageGroup.class}) @RequestBody PostQueryReq form) {
        PageData<PostDTO> page = postService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<PostDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<List<PostDTO>> list(@Validated @RequestBody PostQueryReq form) {
        List<PostDTO> list = postService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<List<PostDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PostDTO> info(@Validated @RequestBody IdReq form) {
        PostDTO data = postService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<PostDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:edit"}, logical = Logical.OR)
    public Result<PostDTO> saveOrUpdate(@Validated @RequestBody PostSaveOrUpdateReq req) {
        PostEntity entity = postService.saveOrUpdateByReq(req);
        PostDTO dto = ConvertUtils.sourceToTarget(entity, PostDTO.class);

        return new Result<PostDTO>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:post:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(postService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        postService.removeById(req.getId());
        // todo 删除关联用户
        return new Result<>();
    }

}
