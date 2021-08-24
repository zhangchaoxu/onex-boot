package com.nb6868.onex.api.modules.msg.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.msg.dao.NoticeLogDao;
import com.nb6868.onex.api.modules.msg.dto.NoticeLogDTO;
import com.nb6868.onex.api.modules.msg.entity.NoticeLogEntity;
import com.nb6868.onex.api.modules.uc.user.SecurityUser;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.service.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class NoticeLogService extends DtoService<NoticeLogDao, NoticeLogEntity, NoticeLogDTO> {


    @Override
    public QueryWrapper<NoticeLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<NoticeLogEntity>(new QueryWrapper<>(), params)
                .eq("state", "state")
                .eq("read", "read")
                .like("content", "content")
                .apply(Const.SQL_FILTER)
                .apply(Const.SQL_FILTER_MY)
                .getQueryWrapper();
    }

    /**
     * 设置为已读
     * @param ids 对应的ids
     */
    public boolean read(List<Long> ids) {
        UserDetail user = SecurityUser.getUser();
        return update().set("readed", 1)
                .in("id", ids)
                .eq("user_id", user.getId()).update(new NoticeLogEntity());
    }

    /**
     * 所有未读消息设置为已读
     */
    public boolean readAllUnread() {
        UserDetail user = SecurityUser.getUser();
        return update().set("readed", 1)
                .eq("readed", 0)
                .eq("user_id", user.getId()).update(new NoticeLogEntity());
    }

}
