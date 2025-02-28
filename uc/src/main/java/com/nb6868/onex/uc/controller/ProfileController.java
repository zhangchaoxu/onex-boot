package com.nb6868.onex.uc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.ProfileParamQueryReq;
import com.nb6868.onex.uc.dto.ProfileParamsSaveOrUpdateReq;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.ParamsService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("UcProfile")
@RequestMapping("/uc/profile/")
@Validated
@Tag(name = "用户资料(我的)")
@Slf4j
public class ProfileController {

    @Autowired
    ParamsService paramsService;
    @Autowired
    UserService userService;

    @PostMapping("userInfo")
    @Operation(summary = "用户信息")
    public Result<UserDTO> userInfo(@Validated @RequestBody BaseReq req) {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);

        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        // todo 补上用户的其他信息
        // data.setRoleCodes(userService.getUserRoleCodes());
        return new Result<UserDTO>().success(data);
    }

    @PostMapping("updateUserParams")
    @Operation(summary = "更新用户个人参数")
    @LogOperation(value = "更新用户个人参数")
    public Result<?> saveOrUpdateUserParams(@Validated @RequestBody ProfileParamsSaveOrUpdateReq req) {
        Long currentUserId = ShiroUtils.getUserId();
        // 先判断是否存在
        ParamsEntity entity = paramsService.lambdaQuery()
                .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.USER.getCode())
                .eq(ParamsEntity::getScope, UcConst.ParamsScopeEnum.PRIVATE.getCode())
                .eq(ParamsEntity::getUserId, currentUserId)
                .eq(ParamsEntity::getCode, req.getCode())
                .last(Const.LIMIT_ONE).one();
        entity = ObjUtil.defaultIfNull(entity, new ParamsEntity());
        entity.setType(UcConst.ParamsTypeEnum.USER.getCode());
        entity.setScope(UcConst.ParamsScopeEnum.PRIVATE.getCode());
        entity.setUserId(currentUserId);
        BeanUtil.copyProperties(req, entity);
        paramsService.saveOrUpdateById(entity);
        return new Result<>();
    }

    @PostMapping("getUserParamsContent")
    @Operation(summary = "获得用户个人参数内容")
    public Result<String> getUserParamsContent(@Validated @RequestBody ProfileParamQueryReq req) {
        Long currentUserId = ShiroUtils.getUserId();
        // 先判断是否存在
        ParamsEntity entity = paramsService.lambdaQuery()
                .eq(ParamsEntity::getType, UcConst.ParamsTypeEnum.USER.getCode())
                .eq(ParamsEntity::getScope, UcConst.ParamsScopeEnum.PRIVATE.getCode())
                .eq(ParamsEntity::getUserId, currentUserId)
                .eq(ParamsEntity::getCode, req.getCode())
                .last(Const.LIMIT_ONE).one();
        return new Result<String>().success(entity == null ? null : entity.getContent());
    }

}
