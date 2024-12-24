package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Configuration
public class TaskConfig {
    @Bean(name = "customTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("custom_task_executor_thread");
        executor.initialize();
        System.out.println("TaskExecutor initialized with thread prefix: default_task_executor_thread");
        return executor;
    }
}
