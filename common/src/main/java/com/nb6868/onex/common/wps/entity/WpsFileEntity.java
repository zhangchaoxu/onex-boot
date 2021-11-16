package com.nb6868.onex.common.wps.entity;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nb6868.onex.common.pojo.json.LongToLongSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jueyue on 20-5-8.
 * see { https://wwo.wps.cn/docs/server/callback-api-standard/get-file-metadata/ }
 */
@Data
public class WpsFileEntity implements Serializable {
    /**
     * 文件id,字符串长度小于40
     */
    private String id;
    /**
     * 文件名
     */
    private String name;
    /**
     * 当前版本号，位数小于11
     */
    private int version = 1;
    /**
     * 文件大小，单位为kb
     */
    private int size;
    /**
     * 创建者id，字符串长度小于40
     */
    private String creator = "0";
    /**
     * 修改者id，字符串长度小于40
     */
    private String modifier = "0";
    /**
     * 创建时间，时间戳，单位为秒
     */
    @JsonSerialize(using = LongToLongSerializer.class)
    private Long create_time = DateUtil.currentSeconds();
    /**
     * 修改时间，时间戳，单位为秒
     */
    @JsonSerialize(using = LongToLongSerializer.class)
    private Long modify_time = DateUtil.currentSeconds();
    /**
     * 文档下载地址
     */
    private String download_url;
    /**
     * 用户权限控制
     */
    private WpsUserAclEntity user_acl = new WpsUserAclEntity();
    /**
     * 水印
     */
    private WpsWatermarkEntity watermark = new WpsWatermarkEntity();
}
