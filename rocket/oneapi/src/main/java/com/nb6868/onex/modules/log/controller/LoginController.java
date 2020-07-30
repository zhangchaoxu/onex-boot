package com.nb6868.onex.modules.log.controller;

import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.log.dto.LoginDTO;
import com.nb6868.onex.modules.log.excel.LoginExcel;
import com.nb6868.onex.modules.log.service.LoginService;
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
 * 登录日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("log/login")
@Validated
@Api(tags = "登录日志")
public class LoginController {

    @Autowired
    private LoginService logLoginService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("log:login:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<LoginDTO> page = logLoginService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("log:login:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<LoginDTO> list = logLoginService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "登录日志", list, LoginExcel.class);
    }

}
