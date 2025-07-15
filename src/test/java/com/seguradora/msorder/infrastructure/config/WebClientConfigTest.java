package com.seguradora.msorder.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WebClientConfigTest {

    @InjectMocks
    private WebClientConfig webClientConfig;

    @Test
    void shouldCreateWebClientBuilder() {
        // When
        WebClient.Builder webClientBuilder = webClientConfig.webClientBuilder();

        // Then
        assertThat(webClientBuilder).isNotNull();
        assertThat(webClientBuilder).isInstanceOf(WebClient.Builder.class);
    }

    @Test
    void shouldCreateDifferentInstancesOfWebClientBuilder() {
        // When
        WebClient.Builder builder1 = webClientConfig.webClientBuilder();
        WebClient.Builder builder2 = webClientConfig.webClientBuilder();

        // Then
        assertThat(builder1).isNotNull();
        assertThat(builder2).isNotNull();
        assertThat(builder1).isNotSameAs(builder2); // Cada chamada deve retornar uma nova instância
    }
}
