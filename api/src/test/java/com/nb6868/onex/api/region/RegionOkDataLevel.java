package com.nb6868.onex.api.region;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class RegionOkDataLevel {

    @Excel(name = "id")
    private Long id;
    @Excel(name = "pid")
    private Long pid;
    @Excel(name = "deep")
    private Integer deep;
    @Excel(name = "name")
    private String name;
    @Excel(name = "pinyin")
    private String pinyin;
    @Excel(name = "pinyin_prefix")
    private String pinyinPrefix;
    @Excel(name = "ext_id")
    private String extId;
    @Excel(name = "ext_name")
    private String extName;

}
