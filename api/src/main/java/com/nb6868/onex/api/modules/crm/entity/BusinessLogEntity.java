package com.nb6868.onex.api.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_business_log")
public class BusinessLogEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 商机id
     */
    private Long businessId;
    /**
     * 记录类型 followup跟进 new创建 edit修改 close关闭
     */
    private String type;
    /**
     * 记录时间，比如跟进时间
     */
    private Date logDate;
    /**
     * 下次跟进时间
     */
    private Date nextFollowDate;
    /**
     * 状态1 阶段1 2 阶段2 3 阶段3 10 赢单 -10 输单 0 无效
     */
    private Integer state;
    /**
     * 内容
     */
    private String content;
    /**
     * 附件
     */
    private String attachment;

}
