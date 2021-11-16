package com.nb6868.onex.common.wps.entity;

import com.nb6868.onex.common.wps.entity.resreq.WpsResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @author JueYue
 * 界面的Token数据,默认不使用
 */
@Data
public class WpsToken extends WpsResponse implements Serializable {

    private int expires_in;
    private String token;
    private String wpsUrl;

}
