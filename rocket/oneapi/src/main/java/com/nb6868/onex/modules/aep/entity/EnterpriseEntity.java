package com.nb6868.onex.modules.aep.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * AEP-企业
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("aep_enterprise")
@Alias("aep_enterprise")
public class EnterpriseEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 联系人
     */
	private String contacts;
    /**
     * 联系电话
     */
	private String telephone;
    /**
     * 备注
     */
	private String remark;
    /**
     * 内容
     */
	private String content;
    /**
     * 状态0 停用 1 正常
     */
	private Integer status;
    /**
     * 区域名称,如浙江省,宁波市,鄞州区
     */
	private String regionName;
    /**
     * 区域编号,如33000,33010,33011
     */
	private String regionCode;
    /**
     * 详细门牌号
     */
	private String address;
    /**
     * 纬度
     */
	private Double lat;
    /**
     * 经度
     */
	private Double lng;
    /**
     * 标签
     */
	private String tags;
    /**
     * 是否推送报警
     */
	private Integer alarmPush;
    /**
     * 漏电处理规则
     */
	private String interruptRule;

	/**
	 * 设备数量
	 */
	@TableField(exist = false)
	private Long deviceCount;
}
