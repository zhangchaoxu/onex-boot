package com.nb6868.onexboot.api.region;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.nb6868.onexboot.common.util.StringUtils;
import lombok.Data;

@Data
public class RegionOkGeo {

    @Excel(name = "id")
    private String id;
    @Excel(name = "geo")
    private String geo;
    @Excel(name = "polygon")
    private String polygon;

    public String getGeo() {
        if (StringUtils.isEmpty(geo) || "empty".equalsIgnoreCase(geo)) {
            return null;
        } else {
            return geo.trim().replaceAll(" ", ",");
        }
    }

    public String getPolygon() {
        if (StringUtils.isEmpty(polygon) || "empty".equalsIgnoreCase(polygon)) {
            return null;
        } else {
            return polygon.trim().replaceAll(",", ";").replaceAll(" ", ",");
        }
    }
}
