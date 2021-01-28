package com.nb6868.onexboot.api.modules.sched.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务(演示Demo，可删除)
 *
 * testTask为spring bean的名称
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component("TestTask")
@Slf4j
public class TestTask implements ITask {

	@Override
	public void run(String params){
		log.info("TestTask定时任务正在执行，参数为：{}", params);
	}

}
