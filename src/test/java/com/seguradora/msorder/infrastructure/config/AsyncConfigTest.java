package com.seguradora.msorder.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AsyncConfigTest {

    @InjectMocks
    private AsyncConfig asyncConfig;

    @Test
    void shouldCreateTaskExecutorWithCorrectConfiguration() {
        // When
        Executor taskExecutor = asyncConfig.taskExecutor();

        // Then
        assertThat(taskExecutor).isNotNull();
        assertThat(taskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);

        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;
        assertThat(executor.getCorePoolSize()).isEqualTo(10);
        assertThat(executor.getMaxPoolSize()).isEqualTo(50);
        assertThat(executor.getQueueCapacity()).isEqualTo(100);
        assertThat(executor.getKeepAliveSeconds()).isEqualTo(60);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("OrderAsync-");
    }

    @Test
    void shouldCreateExecutorWithProperShutdownConfiguration() {
        // When
        Executor taskExecutor = asyncConfig.taskExecutor();

        // Then
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;

        assertThat(executor).isNotNull();
        assertThat(executor.getThreadNamePrefix()).isEqualTo("OrderAsync-");
    }
}
