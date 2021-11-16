package com.nb6868.onex.common.wps.controller;

import com.nb6868.onex.common.wps.service.IEasyPoiWpsConvertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("easypoi/wps/v1/file/convert")
@Slf4j
public class EasyPoiConvertController {

    @GetMapping("version/{version}")
    public byte[] fileVersion(IEasyPoiWpsConvertService service, String taskId, String srcUri, String fileName, String exportType) throws Exception {
        boolean isOk = service.fileConvert(taskId, srcUri, fileName, exportType, null);
        int timeout = 0;
        byte[] bytes = null;
        while (isOk) {
            try {
                Thread.sleep(1000);
                bytes = service.getConvertFile(taskId);
            } catch (Exception e) {
                log.error("文档转换失败", e);
            }
            if (bytes != null || timeout > 120) {
                isOk = false;
            }
            timeout++;
        }
        return bytes;
    }
}
