package com.nb6868.onex.common.jpa;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 公共字段，自动填充值
 * see {https://mybatis.plus/guide/auto-fill-metainfo.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class BaseAutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时间
     */
    protected final static String CREATE_TIME = "createTime";
    /**
     * 创建者id
     */
    protected final static String CREATE_ID = "createId";
    /**
     * 创建者名字
     */
    protected final static String CREATE_NAME = "createName";
    /**
     * 更新时间
     */
    protected final static String UPDATE_TIME = "updateTime";
    /**
     * 更新者id
     */
    protected final static String UPDATE_ID = "updateId";
    /**
     * 更新者名字
     */
    protected final static String UPDATE_NAME = "updateName";
    /**
     * 所在部门id
     */
    protected final static String DEPT_ID = "deptId";
    /**
     * 租户编码
     */
    protected final static String TENANT_CODE = "tenantCode";
    /**
     * 删除标记
     */
    protected final static String DELETED = "deleted";

    /**
     * 插入时填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        ShiroUser user = ShiroUtils.getUser();
        Date now = new Date();

        strictInsertFill(metaObject, DELETED, Integer.class, 0);
        strictInsertFill(metaObject, CREATE_TIME, Date.class, now);
        strictInsertFill(metaObject, UPDATE_TIME, Date.class, now);
        /*if (metaObject.hasGetter(DEPT_ID) && metaObject.getValue(DEPT_ID) == null && user.getDeptId() != null) {
            strictInsertFill(metaObject, DEPT_ID, Long.class, user.getDeptId());
        }*/
        if (metaObject.hasGetter(TENANT_CODE) && metaObject.getValue(TENANT_CODE) == null && user.getTenantCode() != null) {
            strictInsertFill(metaObject, TENANT_CODE, String.class, user.getTenantCode());
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
        ShiroUser user = ShiroUtils.getUser();
        Date now = new Date();
        strictUpdateFill(metaObject, UPDATE_TIME, Date.class, now);
        strictUpdateFill(metaObject, UPDATE_ID, Long.class, user.getId());
        strictUpdateFill(metaObject, UPDATE_NAME, String.class, user.getUsername());
    }

}
