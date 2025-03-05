package com.nb6868.onex.job.service;

import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.job.dao.JobItemDao;
import com.nb6868.onex.job.dto.JobItemDTO;
import com.nb6868.onex.job.entity.JobItemEntity;
import org.springframework.stereotype.Service;

/**
 * 系统-定时任务明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class JobItemService extends DtoService<JobItemDao, JobItemEntity, JobItemDTO> {

}
