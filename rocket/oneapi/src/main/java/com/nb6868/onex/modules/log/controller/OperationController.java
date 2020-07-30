package com.nb6868.onex.modules.log.controller;

import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.log.dto.OperationDTO;
import com.nb6868.onex.modules.log.excel.OperationExcel;
import com.nb6868.onex.modules.log.service.OperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("log/operation")
@Validated
@Api(tags = "操作日志")
public class OperationController {
    @Autowired
    private OperationService logOperationService;

    @GetMapping("page")

    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态  0：失败    1：成功", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "startCreateTime", value = "开始时间", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束时间", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "createName", value = "用户名", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("log:operation:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<OperationDTO> page = logOperationService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("log:operation:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<OperationDTO> list = logOperationService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "操作日志", list, OperationExcel.class);
    }

}
