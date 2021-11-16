package com.nb6868.onex.common.wps.entity.resreq;

import com.nb6868.onex.common.wps.entity.WpsFileHistoryEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileHistoryResponse extends WpsResponse implements Serializable {

    private List<WpsFileHistoryEntity> histories;
}
