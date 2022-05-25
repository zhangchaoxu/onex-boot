# 定时任务
定时执行任务是比较常见的需求，常用的思路有@Scheduled注解、实现SchedulingConfigurer接口和第三方组件Quartz。

## @Scheduled注解

### code
```java
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
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

### code
```java
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "onex.scheduled.guochou", havingValue = "true")
@Slf4j
public class ScheduledConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 任务执行
        taskRegistrar.addTriggerTask(() -> {
            // 执行任务内容
            log.debug("run taskRun @ {}", DateUtil.now());
        }, triggerContext -> {
            // 查询配置中的cron规则
            // String cron = db.query()
            return new CronTrigger(cron).nextExecutionTime(triggerContext);
        });
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
