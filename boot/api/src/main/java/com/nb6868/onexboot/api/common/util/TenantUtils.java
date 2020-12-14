package com.nb6868.onexboot.api.common.util;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;

/**
 * 租户检查工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TenantUtils {

    public static boolean checkTenantId(Long tenantId) {
        UserDetail user = SecurityUser.getUser();

        // 如果是超级管理员，则不进行数据过滤
        if (user.getType() <= UcConst.UserTypeEnum.SYSADMIN.value()) {
            return true;
        } else {
            return user.getTenantId().equals(tenantId);
        }
    }
}
