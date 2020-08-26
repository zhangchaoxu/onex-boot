package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.common.annotation.AccessControl;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 利用Freemarker渲染html页面
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Controller("html")
@RequestMapping("html")
@Api(tags = "html")
public class HtmlController {

    @GetMapping("index")
    @AccessControl
    public String index(ModelMap map) {
        map.put("result", "hello world");
        return "index";
    }

}
