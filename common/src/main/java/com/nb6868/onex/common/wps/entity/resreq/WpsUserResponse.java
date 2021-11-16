package com.nb6868.onex.common.wps.entity.resreq;

import com.nb6868.onex.common.wps.entity.WpsUserEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsUserResponse extends WpsResponse implements Serializable {

    private List<WpsUserEntity> users = new ArrayList<>();
}
