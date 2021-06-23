package com.nb6868.onexboot.api.modules.log.controller;

import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.log.dto.OperationDTO;
import com.nb6868.onexboot.api.modules.log.excel.OperationExcel;
import com.nb6868.onexboot.api.modules.log.service.OperationService;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import io.swagger.annotations.Api;
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
@RequestMapping("/log/operation")
@Validated
@Api(tags = "操作日志")
public class OperationController {
    @Autowired
    OperationService logOperationService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("log:operation:info")
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
