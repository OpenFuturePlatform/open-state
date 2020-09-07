package io.openfuture.state.config

import io.openfuture.state.Application
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.boot.task.TaskExecutorBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.Executor

@Configuration
@EnableScheduling
@EnableAsync
class AsyncConfig(private val taskExecutorBuilder: TaskExecutorBuilder) : AsyncConfigurer {

    override fun getAsyncExecutor(): Executor {
        return taskExecutorBuilder.build()
    }

    override fun getAsyncUncaughtExceptionHandler() = AsyncUncaughtExceptionHandler { ex, method, params ->
        if (!log.isErrorEnabled) {
            return@AsyncUncaughtExceptionHandler
        }

        log.error("Unexpected exception occurred invoking async method: $method, with params: ${params.toList()}", ex)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)
    }

}
