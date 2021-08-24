package onexcoder.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.nb6868.onexcoder.entity.DbConfigRequest;
import com.nb6868.onexcoder.service.TableSchemaService;
import com.nb6868.onexcoder.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 表结构
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Controller
@RequestMapping("/tableSchema")
public class TableSchemaController {

    @Autowired
    TableSchemaService tableSchemaService;

    /**
     * 列表
     */
    @ResponseBody
    @PostMapping("list")
    public Result<?> list(@RequestBody DbConfigRequest request) {
        try {
            return new Result<>().success(tableSchemaService.queryList(request));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>().error();
        }
    }

    /**
     * 生成代码
     */
    @GetMapping("/generateCode")
    public void generateCode(HttpServletResponse response, @RequestParam String base64Request) throws Exception {
        DbConfigRequest request = JSONUtil.toBean(URLDecoder.decode(base64Request, StandardCharsets.UTF_8.name()), DbConfigRequest.class);
        byte[] data = tableSchemaService.generateCode(request);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + request.getTableNames() + ".zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), true, data);
    }

    /**
     * 生成数据库文档
     */
    @GetMapping("/generateDoc")
    public void generateDoc(HttpServletResponse response, @RequestParam String base64Request) throws Exception {
        DbConfigRequest request = JSONUtil.toBean(URLDecoder.decode(base64Request, StandardCharsets.UTF_8.name()), DbConfigRequest.class);

        File file = tableSchemaService.generateDoc(request);
        byte[] data = FileUtil.readBytes(file);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), true, data);
    }

}
