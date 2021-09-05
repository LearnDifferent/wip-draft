package com.github.learndifferent.mtm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 *
 * @author zhou
 */
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private static final int CORE_POOL_SIZE = 3;
    private static final int ALIVE_SECONDS = 30;
    private static final int QUEUE_CAPACITY = 50;

    @Bean("asyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setKeepAliveSeconds(ALIVE_SECONDS);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        // 这里处理的任务不重要，可以允许丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadNamePrefix("custom-async-");
        executor.initialize();
        return executor;
    }

}
