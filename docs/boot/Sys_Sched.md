# 定时任务
包括定时任务管理，定时任务的启停和日志,可在后台启停任务,修改任务的执行周期,便于系统中定时任务需求的集成;
定时执行任务是比较常见的需求，常用的思路有@Scheduled注解、实现SchedulingConfigurer接口和第三方组件Quartz。

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

### 优缺点
优点：灵活、可以自由控制
缺点：比较重、需要引入额外的表
