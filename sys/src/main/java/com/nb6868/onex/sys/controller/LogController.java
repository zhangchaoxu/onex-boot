package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.LogDTO;
import com.nb6868.onex.sys.dto.LogQueryForm;
import com.nb6868.onex.sys.entity.LogEntity;
import com.nb6868.onex.sys.excel.LogExcel;
import com.nb6868.onex.sys.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/sys/log/")
@Validated
@Api(tags = "日志管理", position = 30)
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("page")
    @ApiOperation("分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:log:query")
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody LogQueryForm form) {
        QueryWrapper<LogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<LogDTO> page = logService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions("sys:log:export")
    public void export(@Validated @RequestBody LogQueryForm form, HttpServletResponse response) {
        QueryWrapper<LogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<LogDTO> list = logService.listDto(queryWrapper);

        ExcelUtils.exportExcelToTarget(response, "日志", list, LogExcel.class);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:log:delete")
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        logService.logicDeleteByIds(form.getIds());

        return new Result<>();
    }

}
