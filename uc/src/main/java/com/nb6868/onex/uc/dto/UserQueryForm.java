package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BasePageForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户查询")
public class UserQueryForm extends BasePageForm {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "code,username,real_name,nickname,mobile,remark")
    @ApiModelProperty("搜索关键词")
    private String search;

    @Query
    @ApiModelProperty("租户编码")
    private String tenantCode;

    @Query
    @ApiModelProperty("状态")
    private Integer state;

    @Query
    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("角色id数组")
    private List<Long> roleIds;

}
