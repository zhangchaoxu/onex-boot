package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.common.annotation.AccessControl;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.sys.entity.ShorturlEntity;
import com.nb6868.onexboot.api.modules.sys.service.ShorturlService;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
@Api(tags = "短地址接口")
@RequestMapping("t")
public class TShorturlController {

    @Autowired
    private ShorturlService shorturlService;

    @SneakyThrows
    @GetMapping("{code}")
    @ApiOperation("系统信息")
    @LogOperation("系统信息")
    @AccessControl
    public String redirect(@PathVariable("code") String code, HttpServletResponse response) {
        UserDetail user = SecurityUser.getUser();
        ShorturlEntity entity = shorturlService.getByCode(code);
        if (null == entity) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset='utf-8'");
            response.getWriter().print("not found");
        } else if (entity.getStatus() != 1) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset='utf-8'");
            response.getWriter().print("not open");
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain;charset='utf-8'");
            response.getWriter().print(user.getUsername());
            // 301 redirect
            //return "redirect:" + entity.getUrl();
        }
        return null;
    }

}