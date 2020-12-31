package com.nb6868.onexboot.api.region;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class RegionOkGeo {

    @Excel(name = "id")
    private String id;
    @Excel(name = "geo")
    private String geo;
    @Excel(name = "polygon")
    private String polygon;

}
