package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageReq;
import com.nb6868.onex.common.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户查询")
public class UserQueryReq extends PageReq {

    @Query
    @Schema(description = "关联表id")
    private Long relId;

    @Query
    @Schema(description = "用户名")
    private String username;

    @Query
    @Schema(description = "姓名")
    private String realName;

    @Query
    @Schema(description = "手机号")
    private String mobile;

    /*@Query(type = Query.Type.LIKE_LEFT)
    @Schema(description = "部门编码")
    private String deptCode;*/

    @Query(type = Query.Type.LIKE_LEFT)
    @Schema(description = "区域编码")
    private String areaCode;

    @Query(type = Query.Type.LIKE_LEFT)
    @Schema(description = "岗位编码")
    private String postCode;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "类型")
    private Integer type;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,username,real_name,nickname,mobile,remark")
    @Schema(description = "搜索关键词")
    private String search;

    @Schema(description = "角色id数组")
    private List<Long> roleIds;

    @Schema(description = "需要角色信息, 0/1")
    @EnumValue(message = "角色信息参数为0和1", enumClass = Const.BooleanEnum.class)
    private Integer roleNeeded = 0;

    /*@Schema(description = "角色编码数组")
    private List<String> roleCodes;*/

    @Schema(description = "部门id数组")
    private List<Long> deptIds;

    @Schema(description = "需要部门信息, 0/1")
    @EnumValue(message = "部门信息参数为0和1", enumClass = Const.BooleanEnum.class)
    private Integer deptNeeded = 0;

}
