package com.mansi.pulseops.ai.client;

import com.mansi.pulseops.ai.config.AiProperties;
import com.mansi.pulseops.observability.metrics.PulseOpsMetrics;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BedrockAiClientTest {

    @Test
    void shouldReturnTextFromBedrock() {

        BedrockRuntimeClient client =
                mock(BedrockRuntimeClient.class);

        PulseOpsMetrics metrics =
                mock(PulseOpsMetrics.class);

        AiProperties properties =
                new AiProperties();

        properties.setModelId("test-model");
        properties.setMaxTokens(500);
        properties.setTemperature(0.2);

        Message outputMessage =
                Message.builder()
                        .role(ConversationRole.ASSISTANT)
                        .content(
                                List.of(
                                        ContentBlock.fromText(
                                                "AI analysis result"
                                        )
                                )
                        )
                        .build();

        ConverseResponse response =
                ConverseResponse.builder()
                        .output(
                                ConverseOutput.builder()
                                        .message(outputMessage)
                                        .build()
                        )
                        .build();

        when(
                client.converse(
                        any(ConverseRequest.class)
                )
        ).thenReturn(response);

        BedrockAiClient aiClient =
                new BedrockAiClient(
                        client,
                        properties,
                        metrics
                );

        String result =
                aiClient.analyze(
                        "test prompt"
                );

        assertEquals(
                "AI analysis result",
                result
        );

        verify(metrics).recordRequest();
        verify(metrics).recordSuccess();
        verify(metrics).recordLatency(anyLong());
    }
    @Test
    void shouldReturnFallbackResponse() {

        BedrockRuntimeClient client =
                mock(BedrockRuntimeClient.class);

        PulseOpsMetrics metrics =
                mock(PulseOpsMetrics.class);

        AiProperties properties =
                new AiProperties();

        BedrockAiClient aiClient =
                new BedrockAiClient(
                        client,
                        properties,
                        metrics
                );

        String result =
                aiClient.fallbackAnalyze(
                        "test prompt",
                        new RuntimeException(
                                "Bedrock unavailable"
                        )
                );

        assertTrue(
                result.contains(
                        "AI-generated analysis is temporarily unavailable"
                )
        );

        assertTrue(
                result.contains(
                        "No LLM inference was performed"
                )
        );

        verify(metrics).recordFallback();
    }
}