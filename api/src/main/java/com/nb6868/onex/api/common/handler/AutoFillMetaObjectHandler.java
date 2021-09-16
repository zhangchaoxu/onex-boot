package com.nb6868.onex.api.common.handler;

import com.nb6868.onex.api.modules.uc.user.SecurityUser;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 公共字段，自动填充值
 * see {https://mybatis.plus/guide/auto-fill-metainfo.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时间
     */
    private final static String CREATE_TIME = "createTime";
    /**
     * 创建者id
     */
    private final static String CREATE_ID = "createId";
    /**
     * 创建者名字
     */
    private final static String CREATE_NAME = "createName";
    /**
     * 更新时间
     */
    private final static String UPDATE_TIME = "updateTime";
    /**
     * 更新者id
     */
    private final static String UPDATE_ID = "updateId";
    /**
     * 更新者名字
     */
    private final static String UPDATE_NAME = "updateName";
    /**
     * 所在部门id
     */
    private final static String DEPT_ID = "deptId";
    /**
     * 租户id
     */
    private final static String TENANT_ID = "tenantId";
    /**
     * 租户名称
     */
    private final static String TENANT_NAME = "tenantName";
    /**
     * 删除标记
     */
    private final static String DELETED = "deleted";

    /**
     * 插入时填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        UserDetail user = SecurityUser.getUser();
        Date date = new Date();

        strictInsertFill(metaObject, DELETED, Integer.class, 0);
        strictInsertFill(metaObject, CREATE_TIME, Date.class, date);
        strictInsertFill(metaObject, UPDATE_TIME, Date.class, date);
        if (metaObject.hasGetter(DEPT_ID) && metaObject.getValue(DEPT_ID) == null && user.getDeptId() != null) {
            strictInsertFill(metaObject, DEPT_ID, Long.class, user.getDeptId());
        }
        if (metaObject.hasGetter(TENANT_ID) && metaObject.getValue(TENANT_ID) == null && user.getTenantId() != null) {
            strictInsertFill(metaObject, TENANT_ID, Long.class, user.getTenantId());
            strictInsertFill(metaObject, TENANT_NAME, String.class, user.getTenantName());
        }
        strictInsertFill(metaObject, CREATE_ID, Long.class, user.getId());
        strictInsertFill(metaObject, CREATE_NAME, String.class, user.getUsername());
        strictInsertFill(metaObject, UPDATE_ID, Long.class, user.getId());
        strictInsertFill(metaObject, UPDATE_NAME, String.class, user.getUsername());
    }

    /**
     * 更新时填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        UserDetail user = SecurityUser.getUser();
        Date date = new Date();
        strictUpdateFill(metaObject, UPDATE_TIME, Date.class, date);
        strictUpdateFill(metaObject, UPDATE_ID, Long.class, user.getId());
        strictUpdateFill(metaObject, UPDATE_NAME, String.class, user.getUsername());
    }

}
