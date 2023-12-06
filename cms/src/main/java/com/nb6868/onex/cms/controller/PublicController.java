package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.AxdDTO;
import com.nb6868.onex.cms.dto.SiteDTO;
import com.nb6868.onex.cms.entity.AxdEntity;
import com.nb6868.onex.cms.entity.SiteEntity;
import com.nb6868.onex.cms.service.AxdService;
import com.nb6868.onex.cms.service.SiteService;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 站点
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("CmsPublic")
@RequestMapping("/cms/public")
@AccessControl("/cms/public/**")
@Validated
@Tag(name="CMS开放接口")
public class PublicController {

    @Autowired
    private SiteService siteService;
    @Autowired
    private AxdService axdService;

    @GetMapping("getAxdListByPosition")
    @Operation(summary = "获取指定位置的广告列表")
    public Result<?> getAxdListByPosition(@NotEmpty(message = "位置参数不能为空") @RequestParam String position) {
        List<AxdEntity> entityList = axdService.listByPosition(position);
        List<AxdDTO> list = ConvertUtils.sourceToTarget(entityList, AxdDTO.class);

        return new Result<>().success(list);
    }

    @GetMapping("getAxdByPosition")
    @Operation(summary = "获取指定位置的广告")
    public Result<?> getAxdByPosition(@NotEmpty(message = "位置参数不能为空") @RequestParam String position) {
        AxdEntity entity = axdService.getByPosition(position);
        AxdDTO dto = ConvertUtils.sourceToTarget(entity, AxdDTO.class);

        return new Result<>().success(dto);
    }

    @GetMapping("getSiteInfoByCode")
    @Operation(summary = "通过code获取站点详情")
    public Result<?> getSiteInfoByCode(@NotNull(message = "{code.require}") @RequestParam String code) {
        SiteEntity entity = siteService.getOneByColumn("code", code);
        SiteDTO dto = ConvertUtils.sourceToTarget(entity, SiteDTO.class);
        return new Result<>().success(dto);
    }

}
