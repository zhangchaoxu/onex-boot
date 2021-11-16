package com.nb6868.onex.common.wps.controller;

import com.nb6868.onex.common.wps.entity.WpsUserEntity;
import com.nb6868.onex.common.wps.entity.resreq.WpsUserRequest;
import com.nb6868.onex.common.wps.entity.resreq.WpsUserResponse;
import com.nb6868.onex.common.wps.service.IEasyPoiWpsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("easypoi/wps/v1/3rd/file")
public class EasyPoiUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPoiUserController.class);

    @Autowired(required = false)
    private IEasyPoiWpsUserService userService;

    /**
     * 获取用户信息
     */
    @PostMapping("info")
    public WpsUserResponse userInfo(@RequestBody WpsUserRequest list) {
        LOGGER.info("获取用户信息param:{}", list);
        WpsUserResponse response = new WpsUserResponse();
        for (int i = 0; i < list.getIds().size(); i++) {
            if (userService != null) {
                response.getUsers().add(userService.getUser(list.getIds().get(i)));
            } else {
                response.getUsers().add(new WpsUserEntity());
            }
        }
        return response;
    }
}
