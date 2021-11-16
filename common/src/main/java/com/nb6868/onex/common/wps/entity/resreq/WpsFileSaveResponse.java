package com.nb6868.onex.common.wps.entity.resreq;

import com.nb6868.onex.common.wps.entity.WpsFileEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileSaveResponse extends WpsResponse implements Serializable {

    public WpsFileSaveResponse() {
    }

    public WpsFileSaveResponse(WpsFileEntity file) {
        this.file = file;
    }

    private WpsFileEntity file;
}
