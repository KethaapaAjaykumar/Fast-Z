package com.example.Social.Media.Platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient;

    public Mono<Map<String, Object>> fetchExternalData(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    public Mono<Void> notifyExternalSystem(String message) {
        // Example: Sending notification to an external analytics service
        return webClient.post()
                .uri("https://api.externalanalytics.com/events")
                .bodyValue(Map.of("message", message))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
