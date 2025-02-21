package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ID基础请求")
public class BaseIdReq extends BaseReq {

    @Query
    @Schema(description = "id")
    @Null(message = "ID参数不能有值", groups = AddGroup.class)
    @NotNull(message = "ID参数不能为空", groups = UpdateGroup.class)
    private Long id;

    @Schema(description = "是否存在id，用来判断还是新增")
    @JsonIgnore
    public boolean hasId() {
        //return id != null && id > 0;
        return id != null;
    }

}
