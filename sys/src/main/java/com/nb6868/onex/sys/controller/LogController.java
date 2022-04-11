package com.nb6868.onex.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdsForm;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.dto.LogDTO;
import com.nb6868.onex.sys.dto.LogQueryForm;
import com.nb6868.onex.sys.entity.LogEntity;
import com.nb6868.onex.sys.excel.LogExcel;
import com.nb6868.onex.sys.service.LogService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/log")
@Validated
@Api(tags = "日志")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("page")
    @ApiOperation("分页")
    @QueryDataScope(tenantFilter = true)
    @RequiresPermissions("sys:log:query")
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody LogQueryForm form) {
        QueryWrapper<LogEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        PageData<LogDTO> page = logService.pageDto(form.getPage(), queryWrapper);

        return new Result<>().success(page);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:log:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<LogDTO> list = logService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "日志", list, LogExcel.class);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:log:deleteBatch")
    public Result<?> deleteBatch(@Validated @RequestBody IdsForm form) {
        logService.logicDeleteByIds(form.getIds());

        return new Result<>();
    }

}
