package com.h3c.ywrj.dzkf.hkcustoms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by @author Jeff on 2019-12-16 15:49
 */
@Configuration
@EnableAsync
@Slf4j
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public class AsyncConfig {
    @Value("${thread.pool.core-size}")
    private int coreSize;
    @Value("${thread.pool.max-size}")
    private int maxSize;
    @Value("${thread.pool.queue-capacity}")
    private int queueCapacity;
    @Value("${thread.pool.keep-alive}")
    private int keepAlive;
    @Value("${thread.pool.name-prefix}")
    private String namePrefix;

    // 核心线程数（setCorePoolSize）：线程池创建时候初始化的线程数
    // 最大线程数（setMaxPoolSize）：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
    // 缓冲队列（setQueueCapacity）：用来缓冲执行任务的队列
    // 允许线程的空闲时间（setKeepAliveSeconds）：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
    // 线程池名的前缀（setThreadNamePrefix）：设置好了之后可以方便我们定位处理任务所在的线程池
    // 线程池对拒绝任务的处理策略（setRejectedExecutionHandler）：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute
    // 方法的调用线程中运行被拒绝的任务（setWaitForTasksToCompleteOnShutdown）；如果执行程序已关闭，则会丢弃该任务
    // setWaitForTasksToCompleteOnShutdown（true）该方法就是这里的关键，用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean，这样这些异步任务的销毁就会先于Redis线程池的销毁。
    // 同时，这里还设置了setAwaitTerminationSeconds(60)，该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
//        log.info("Creating Async Task Executor: {}, {}, {}, {}, {}", coreSize, maxSize, queueCapacity, keepAlive, namePrefix);
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(namePrefix);
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 调度器shutdown被调用时等待当前被调度的任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时长
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
