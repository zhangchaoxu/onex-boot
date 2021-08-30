package com.nb6868.onex.api.modules.log.controller;

import com.nb6868.onex.api.modules.log.service.ErrorService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.api.common.util.ExcelUtils;
import com.nb6868.onex.api.modules.log.dto.ErrorDTO;
import com.nb6868.onex.api.modules.log.excel.ErrorExcel;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
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
 * 异常日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/log/error")
@Validated
@Api(tags = "异常日志")
public class ErrorController {

    @Autowired
    ErrorService logErrorService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("log:error:info")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ErrorDTO> page = logErrorService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("log:error:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ErrorDTO> list = logErrorService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "异常日志", list, ErrorExcel.class);
    }

}
