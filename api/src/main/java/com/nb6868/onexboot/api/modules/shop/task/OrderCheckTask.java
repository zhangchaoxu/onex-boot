package com.nb6868.onexboot.api.modules.shop.task;

import com.nb6868.onexboot.api.modules.sched.task.ITask;
import com.nb6868.onexboot.api.modules.shop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单检查定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component("OrderCheckTask")
public class OrderCheckTask implements ITask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    OrderService orderService;

    @Override
    public void run(String params) {
        logger.debug("OrderCheckTask定时任务正在执行，参数为：{}", params);
        orderService.cancelUnPaidOrder(30 * 60);
    }

}
