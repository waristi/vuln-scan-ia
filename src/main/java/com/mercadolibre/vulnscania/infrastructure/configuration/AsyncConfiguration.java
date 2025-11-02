package com.mercadolibre.vulnscania.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for asynchronous processing support.
 * 
 * <p>Enables @Async annotation support for Spring beans. This is used primarily
 * for event listeners to ensure that domain event processing doesn't block
 * the main transaction.</p>
 * 
 * <p>Benefits of @Async for event listeners:</p>
 * <ul>
 *   <li>Non-blocking: Main transaction completes faster</li>
 *   <li>Resilience: Event processing failures don't affect main flow</li>
 *   <li>Scalability: Events processed in separate thread pool</li>
 *   <li>Better user experience: Faster response times</li>
 * </ul>
 * 
 * <p>Note: By default, Spring uses a SimpleAsyncTaskExecutor with unlimited threads.
 * For production, consider configuring a ThreadPoolTaskExecutor with appropriate limits.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    // Default async configuration
    // For production, consider customizing the executor:
    
    /*
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-event-");
        executor.initialize();
        return executor;
    }
    */
}

