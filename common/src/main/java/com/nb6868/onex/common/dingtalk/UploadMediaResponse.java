package com.nb6868.onex.common.dingtalk;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传媒体文件返回内容
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadMediaResponse extends BaseResponse {

    /**
     * 媒体文件类型
     */
    private String type;
    /**
     * 媒体文件上传后获取的唯一标识
     */
    private String media_id;
    /**
     * 媒体文件上传时间戳
     */
    private Long created_at;

}
