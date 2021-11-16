package com.nb6868.onex.common.wps.entity.resreq;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileHistoryRequest extends WpsResponse implements Serializable {

    private String id;
    private int offset;
    private int count;
}
