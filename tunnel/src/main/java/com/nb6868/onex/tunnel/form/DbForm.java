package com.nb6868.onex.tunnel.form;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "数据查询请求")
public class DbForm extends BaseForm {

    @Schema(description = "数据源")
    private String ds;

    @Schema(description = "数据库url")
    private String url;

    @Schema(description = "数据库用户名")
    private String username;

    @Schema(description = "数据库密码")
    private String password;

}
