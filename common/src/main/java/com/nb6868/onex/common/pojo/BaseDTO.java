package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础DTO
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public abstract class BaseDTO implements Serializable {

    @ApiModelProperty(value = "id")
    @Null(message = "{id.null}", groups = AddGroup.class)
    @NotNull(message = "{id.require}", groups = UpdateGroup.class)
    private Long id;

    @ApiModelProperty(value = "创建者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createTime;

    @ApiModelProperty(value = "更新者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updateId;

    @ApiModelProperty(value = "更新时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除")
    @JsonIgnore
    private Integer deleted;

    @ApiModelProperty(value = "是否存在id，用来判断还是新增")
    @JsonIgnore
    public boolean hasId() {
        return id != null && id > 0;
    }

}
