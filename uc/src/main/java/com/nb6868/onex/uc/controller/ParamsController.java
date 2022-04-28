package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.ParamsInfoQueryForm;
import com.nb6868.onex.uc.entity.ParamsEntity;
import com.nb6868.onex.uc.service.ParamsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uc/params/")
@Validated
@Api(tags = "参数管理", position = 10)
public class ParamsController {

    @Autowired
    private ParamsService tenantParamsService;

    @PostMapping("infoByCode")
    @AccessControl
    @ApiOperation(value = "通过编码获取配置信息")
    public Result<?> infoByCode(@Validated @RequestBody ParamsInfoQueryForm form) {
        QueryWrapper<ParamsEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        ParamsEntity data = tenantParamsService.getOne(queryWrapper);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(UcConst.ParamsScopeEnum.PUBLIC.value().equalsIgnoreCase(data.getScope()), "参数非公开");
        return new Result<>().success(data.getContent());
    }

}
