package com.nb6868.onex.modules.aep.task;

import com.nb6868.onex.modules.sched.task.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 定时同步AEP产品信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component("AepProductSyncTask")
public class AepProductSyncTask implements ITask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(String params) {
        logger.debug("TestTask定时任务正在执行，参数为：{}", params);

    }

}
