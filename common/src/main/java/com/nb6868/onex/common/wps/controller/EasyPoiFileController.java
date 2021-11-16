package com.nb6868.onex.common.wps.controller;

import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.wps.entity.WpsToken;
import com.nb6868.onex.common.wps.entity.resreq.*;
import com.nb6868.onex.common.wps.service.IEasyPoiWpsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@RestController
@RequestMapping("easypoi/wps/v1/3rd/file")
@Slf4j
public class EasyPoiFileController {

    @Autowired(required = false)
    private IEasyPoiWpsService easyPoiWpsService;

    /**
     * 获取网络文件预览URL
     *
     * @param filePath
     * @return t
     */
    @GetMapping("getViewUrl")
    public WpsToken getViewUrl(String filePath) {
        log.info("getViewUrlWebPath：filePath={}", filePath);
        return easyPoiWpsService.getViewUrl(decode(filePath));
    }

    /**
     * 获取文件元数据
     */
    @GetMapping("info")
    public WpsFileResponse getFileInfo(HttpServletRequest request, @RequestParam(value = "_w_userid", required = false) String userId,
                                       @RequestParam("_w_filepath") String filePath) {
        String fileId = request.getHeader("x-weboffice-file-id");
        log.info("获取文件元数据userId:{},path:{},fileId:{}", userId, filePath, fileId);
        WpsFileResponse response = easyPoiWpsService.getFileInfo(userId, decode(filePath), fileId);
        log.info("返回数据:{}", JSONUtil.toJsonStr(response));
        return response;
    }

    /**
     * 通知此文件目前有哪些人正在协作
     */
    @PostMapping("online")
    public WpsResponse fileOnline(HttpServletRequest request, @RequestBody WpsUserRequest list) {
        log.info("通知此文件目前有哪些人正在协作param:{}", list);
        String fileId = request.getHeader("x-weboffice-file-id");
        easyPoiWpsService.fileOnline(fileId, list);
        return WpsResponse.success();
    }

    /**
     * 上传文件新版本
     */
    @PostMapping("save")
    public WpsFileSaveResponse fileSave(HttpServletRequest request,
                                        @RequestParam(value = "_w_userid", required = false) String userId,
                                        @RequestBody MultipartFile file) {
        log.info("上传文件新版本");
        String fileId = request.getHeader("x-weboffice-file-id");
        return new WpsFileSaveResponse(easyPoiWpsService.fileSave(fileId, userId, file));
    }

    /**
     * 获取特定版本的文件信息
     */
    @GetMapping("version/{version}")
    public WpsFileSaveResponse fileVersion(HttpServletRequest request, @PathVariable Integer version,
                                           @RequestParam("_w_filepath") String filePath) {
        log.info("获取特定版本的文件信息version:{}", version);
        String fileId = request.getHeader("x-weboffice-file-id");
        return new WpsFileSaveResponse(easyPoiWpsService.getFileInfoOfVersion(fileId, decode(filePath), version));
    }

    /**
     * 文件重命名
     */
    @PutMapping("rename")
    public WpsResponse fileRename(HttpServletRequest request, @RequestBody WpsRenameRequest req,
                                  @RequestParam(value = "_w_userid", required = false) String userId) {
        log.info("文件重命名param:{},userId:{}", req, userId);
        String fileId = request.getHeader("x-weboffice-file-id");
        easyPoiWpsService.rename(fileId, userId, req.getName());
        return WpsResponse.success();
    }

    /**
     * 获取所有历史版本文件信息
     */
    @PostMapping("history")
    public WpsFileHistoryResponse fileHistory(@RequestBody WpsFileHistoryRequest req) {
        log.info("获取所有历史版本文件信息param:{}", req);
        return easyPoiWpsService.getHistory(req);
    }

    /**
     * 新建文件
     */
    @PostMapping("new")
    public WpsResponse fileNew(@RequestBody MultipartFile file, @RequestParam(value = "_w_userid", required = false) String userId) {
        log.info("新建文件,只打印不实现,userid:{}", userId);
        return WpsResponse.success();
    }

    /**
     * 回调通知
     */
    @PostMapping("onnotify")
    public WpsResponse onNotify(@RequestBody Map obj) {
        log.info("回调通知,只打印不实现,param:{}", obj);
        return WpsResponse.success();
    }

    public String decode(String filePath) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return filePath;
    }

}
