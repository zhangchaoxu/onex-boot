package com.nb6868.onex.common.wps.entity.resreq;

import com.nb6868.onex.common.wps.entity.WpsFileEntity;
import com.nb6868.onex.common.wps.entity.WpsUserEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取文件返回对象
 *
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileResponse extends WpsResponse implements Serializable {

    private WpsFileEntity file;

    private WpsUserEntity user = new WpsUserEntity();
}
