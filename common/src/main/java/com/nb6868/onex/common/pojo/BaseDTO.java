package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础DTO
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public abstract class BaseDTO implements Serializable {

     @Schema(description = "id")
    @Null(message = "{id.null}", groups = AddGroup.class)
    @NotNull(message = "{id.require}", groups = UpdateGroup.class)
    private Long id;

     @Schema(description = "创建者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createId;

     @Schema(description = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createTime;

     @Schema(description = "更新者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updateId;

     @Schema(description = "更新时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updateTime;

     @Schema(description = "逻辑删除")
    @JsonIgnore
    private Integer deleted;

     @Schema(description = "是否存在id，用来判断还是新增")
    @JsonIgnore
    public boolean hasId() {
        return id != null && id > 0;
    }

}
