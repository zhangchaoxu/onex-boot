package com.nb6868.onex.common.async;

import cn.hutool.core.lang.Dict;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.pojo.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController("AsyncTaskController")
@RequestMapping("/asyncTask")
@Validated
@Api(tags = "异步任务")
public class AsyncTaskController {

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @GetMapping("asyncTaskExecutorStatus")
    @AccessControl(value = "/asyncTaskExecutorStatus")
    public Result<?> getThreadInfo() {
        ThreadPoolExecutor threadPoolExecutor = asyncExecutor.getThreadPoolExecutor();
        Dict dict = Dict.create()
                .set("taskCount", threadPoolExecutor.getTaskCount())
                .set("completedTaskCount", threadPoolExecutor.getCompletedTaskCount())
                .set("activeCount", threadPoolExecutor.getActiveCount())
                .set("queueSize", threadPoolExecutor.getQueue().size())
                .set("queueRemainingCapacity", threadPoolExecutor.getQueue().remainingCapacity());
        return new Result<>().success(dict);
    }

}
