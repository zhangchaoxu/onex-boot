package com.nb6868.onex.sys.service;

import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.sys.dao.MsgLogDao;
import com.nb6868.onex.sys.dto.MsgLogDTO;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import org.springframework.stereotype.Service;

/**
 * 消息发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MsgLogService extends DtoService<MsgLogDao, MsgLogEntity, MsgLogDTO> {

}

