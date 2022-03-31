package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.CommonForm;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.service.MenuScopeService;
import com.nb6868.onex.uc.service.RoleService;
import com.nb6868.onex.uc.service.RoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/uc/role")
@Validated
@Api(tags="角色管理", position = 30)
public class RoleController {

	@Autowired
	private RoleService roleService;
	@Autowired
	private MenuScopeService menuScopeService;
	@Autowired
	private RoleUserService roleUserService;

	@GetMapping("page")
	@ApiOperation("分页")
	@RequiresPermissions("uc:role:page")
	public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params){
		PageData<RoleDTO> page = roleService.pageDto(params);

		return new Result<>().success(page);
	}

	@GetMapping("list")
	@ApiOperation("列表")
	@RequiresPermissions("uc:role:list")
	public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params){
		List<RoleDTO> data = roleService.listDto(params);

		return new Result<>().success(data);
	}

	@GetMapping("info")
	@ApiOperation("信息")
	@RequiresPermissions("uc:role:info")
	public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id){
		RoleDTO data = roleService.getDtoById(id);
		AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

		// 查询角色对应的菜单
		List<Long> menuIdList = menuScopeService.getMenuIdListByRoleId(id);
		data.setMenuIdList(menuIdList);

		return new Result<>().success(data);
	}

	@PostMapping("save")
	@ApiOperation("保存")
	@LogOperation("保存")
	@RequiresPermissions("uc:role:save")
	public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RoleDTO dto){
		roleService.saveDto(dto);

		return new Result<>().success(dto);
	}

	@PostMapping("update")
	@ApiOperation("修改")
	@LogOperation("修改")
	@RequiresPermissions("uc:role:update")
	public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody RoleDTO dto){
		roleService.updateDto(dto);

		return new Result<>().success(dto);
	}

	@PostMapping("delete")
	@ApiOperation("删除")
	@LogOperation("删除")
	@RequiresPermissions("uc:role:delete")
	public Result<?> delete(@RequestBody IdForm form) {
		// 判断数据是否存在
		RoleEntity data = roleService.getById(form.getId());
		AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
		// 删除数据
		roleService.logicDeleteById(data.getId());
		// 删除角色菜单关联关系
		menuScopeService.deleteByRoleCodes(Collections.singletonList(data.getCode()));
		// 删除角色用户关联关系
		roleUserService.deleteByRoleCodes(Collections.singletonList(data.getCode()));
		return new Result<>();
	}

}
