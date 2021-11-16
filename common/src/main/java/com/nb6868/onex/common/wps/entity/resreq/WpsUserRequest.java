package com.nb6868.onex.common.wps.entity.resreq;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsUserRequest  implements Serializable {

    List<String> ids;
}
