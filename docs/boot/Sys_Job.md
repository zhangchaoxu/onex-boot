# 定时任务
适用于需求不高的定时任务场景，支持定时任务的添加，后台启动、暂停、修改执行周期等简单操作。
Onex使用SchedulingConfigurer封装了一套定时任务的管理工具。

## 设计思路
提供定时任务(job)和任务日志(job_log)两张表    
定时任务表中定义任务的状态、参数、cron等信息    
任务日志中记录每次执行的状态、结果等信息

## 使用
1. 数据库中导入[sys_job.sql](./sql/sys_job.sql)

2. 引入依赖
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>job</artifactId>
    <version>${onex.version}</version>
</dependency>
```

3. 代码中加入定时任务的代码
```java
@Service("TestJob")
@Slf4j
public class TestJob extends AbstractJobRunService {

    @Override
    public JobRunResult run(JSONObject runParams, Long jobLogId) {
        log.error("run jobLogId={},param={}", jobLogId, runParams);
        Dict dict = Dict.create().set("sss", "sss");
        // 若有必要，更具jobLogId更新执行状态
        return new JobRunResult(dict);
    }
}
```

4. 在数据库中加入定时任务记录，code为上述代码中Service的名称

5. 重新部署工程后，可以对任务做管理

## 定时任务实现方式比较
定时执行任务是比较常见的需求，常用的思路有@Scheduled注解、实现SchedulingConfigurer接口、第三方组件(比如Quartz、xxl-job)。         
对于简单需求推荐使用@Scheduled和SchedulingConfigurer，对于定时任务要求较高有分布等要求的推荐使用xxl-job.

## @Scheduled注解

### code
```java
@Configuration      // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class StaticScheduleTask {
    //3.添加定时任务
    @Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedDelay=5000)
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
}
```

### 优缺点
优点: 简单轻量
缺点：硬编码，任务的规则、启停都需要修改代码和部署

## 实现SchedulingConfigurer接口
```java
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "xxxx.scheduled.enable", havingValue = "true")
@Slf4j
public class ScheduledConfig implements SchedulingConfigurer {

    @Autowired
    private ParamsService paramsService;

    /**
     * Spring初始化时候执行
     * 获取需要执行的任务，添加到定时器里
     *
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 从数据库或者其它配置中读取需要执行的任务列表
        paramsService.query()
                .select("code")
                .likeRight("code", "TEST_SCHEDULE")
                .list()
                .forEach(paramsEntity -> initTrigger(taskRegistrar, paramsEntity.getCode()));
    }

    /**
     * 添加trigger
     */
    private void initTrigger(ScheduledTaskRegistrar taskRegistrar, String paramsCode) {
        taskRegistrar.addTriggerTask(() -> {
            // 配置参数要再从数据库读一遍，否则不会变更
            JSONObject params = paramsService.getSystemContentJson(paramsCode);
            if (params != null && StrUtil.isNotBlank(params.getStr("taskName"))) {
                try {
                    ReflectUtil.invoke(SpringUtil.getBean(params.getStr("taskName")), "run", params);
                    log.info("run {} @ {}", params.getStr("taskName"), DateUtil.now());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("error run {}", params.getStr("taskName"));
                }
            }
        }, triggerContext -> {
            log.info("TriggerTask next Trigger");
            // 配置参数要再从数据库读一遍，否则不会变更
            JSONObject params = paramsService.getSystemContentJson(paramsCode);
            if (params != null && StrUtil.isNotBlank(params.getStr("cron"))) {
                return new CronTrigger(params.getStr("cron")).nextExecutionTime(triggerContext);
            } else {
                return null;
            }
        });
    }

}
```

## 自定义Task
```java
@Component("TestTask")
@Slf4j
public class TestTask {

    /**
     * 任务执行
     * @param json 执行参数
     */
    public void run(JSONObject json) {
        log.info("scheduled TestTask start, param = {}", json.toString());
    }
}
```

### 优缺点
优点：简单轻量、任务的配置可以存在数据库
缺点：规则的生效需要在下次周期后/当cron错误时，会导致任务被停止/无法控制任务的启停

## 第三方组件Quartz
sched模块是Quartz实现的定时任务，废弃不再继续支持

### 优缺点
优点：灵活、可以自由控制
缺点：比较重、需要引入额外的表
