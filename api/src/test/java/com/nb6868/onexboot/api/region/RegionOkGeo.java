package com.nb6868.onexboot.api.region;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.util.StrUtil;
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
        if (StrUtil.isEmpty(geo) || "empty".equalsIgnoreCase(geo)) {
            return null;
        } else {
            return geo.trim().replaceAll(" ", ",");
        }
    }

    public String getPolygon() {
        if (StrUtil.isEmpty(polygon) || "empty".equalsIgnoreCase(polygon)) {
            return null;
        } else {
            return polygon.trim().replaceAll(",", ";").replaceAll(" ", ",");
        }
    }
}
