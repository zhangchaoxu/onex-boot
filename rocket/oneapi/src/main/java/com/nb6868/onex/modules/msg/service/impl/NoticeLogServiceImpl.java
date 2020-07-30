package com.nb6868.onex.modules.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.modules.msg.dao.NoticeLogDao;
import com.nb6868.onex.modules.msg.dto.NoticeLogDTO;
import com.nb6868.onex.modules.msg.entity.NoticeLogEntity;
import com.nb6868.onex.modules.msg.service.NoticeLogService;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import com.nb6868.onex.modules.uc.user.UserDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class NoticeLogServiceImpl extends CrudServiceImpl<NoticeLogDao, NoticeLogEntity, NoticeLogDTO> implements NoticeLogService {

    @Override
    public QueryWrapper<NoticeLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<NoticeLogEntity>(new QueryWrapper<>(), params)
                .eq("status", "status")
                .eq("read", "read")
                .like("content", "content")
                .apply(Const.SQL_FILTER)
                .apply(Const.SQL_FILTER_MY)
                .getQueryWrapper();
    }

    @Override
    public boolean read(List<Long> ids) {
        UserDetail user = SecurityUser.getUser();
        return update().set("readed", 1)
                .in("id", ids)
                .eq("user_id", user.getId()).update(new NoticeLogEntity());
    }

    @Override
    public boolean readAllUnread() {
        UserDetail user = SecurityUser.getUser();
        return update().set("readed", 1)
                .eq("readed", 0)
                .eq("user_id", user.getId()).update(new NoticeLogEntity());
    }

}
