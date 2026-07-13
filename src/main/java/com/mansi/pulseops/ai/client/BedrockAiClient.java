package com.mansi.pulseops.ai.client;

import com.mansi.pulseops.ai.config.AiProperties;
import com.mansi.pulseops.ai.exception.AiProviderException;
import com.mansi.pulseops.observability.metrics.PulseOpsMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

@Component
public class BedrockAiClient implements AiClient {

    private static final Logger log =
            LoggerFactory.getLogger(
                    BedrockAiClient.class
            );

    private final BedrockRuntimeClient client;
    private final AiProperties properties;
    private final PulseOpsMetrics metrics;

    public BedrockAiClient(
            BedrockRuntimeClient client,
            AiProperties properties,
            PulseOpsMetrics metrics
    ) {
        this.client = client;
        this.properties = properties;
        this.metrics = metrics;
    }

    @Override
    @Retry(name = "bedrockAi")
    @CircuitBreaker(
            name = "bedrockAi",
            fallbackMethod = "fallbackAnalyze"
    )
    public String analyze(String prompt) {

        metrics.recordRequest();

        long startTime =
                System.nanoTime();

        try {
            Message userMessage =
                    Message.builder()
                            .role(ConversationRole.USER)
                            .content(
                                    ContentBlock.fromText(prompt)
                            )
                            .build();

            InferenceConfiguration inferenceConfig =
                    InferenceConfiguration.builder()
                            .maxTokens(
                                    properties.getMaxTokens()
                            )
                            .temperature(
                                    (float) properties.getTemperature()
                            )
                            .build();

            ConverseRequest request =
                    ConverseRequest.builder()
                            .modelId(
                                    properties.getModelId()
                            )
                            .messages(userMessage)
                            .inferenceConfig(inferenceConfig)
                            .build();

            ConverseResponse response =
                    client.converse(request);

            if (response.output() == null
                    || response.output().message() == null
                    || response.output()
                    .message()
                    .content()
                    .isEmpty()) {

                throw new AiProviderException(
                        "Bedrock returned an empty response",
                        null
                );
            }

            String result =
                response.output()
                .message()
                .content()
                .stream()
                .map(ContentBlock::text)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() ->
                        new AiProviderException(
                                "Bedrock response contained no text",
                                null
                        )
                );

            metrics.recordSuccess();

            return result;

        } catch (AiProviderException exception) {

            metrics.recordFailure();
            throw exception;

        } catch (Exception exception) {

            metrics.recordFailure();

            log.error(
                    "Bedrock AI invocation failed modelId={}",
                    properties.getModelId(),
                    exception
            );

            throw new AiProviderException(
                    "Bedrock AI invocation failed",
                    exception
            );

        } finally {

            metrics.recordLatency(
                    System.nanoTime() - startTime
            );
        }
    }

    public String fallbackAnalyze(
            String prompt,
            Throwable throwable
    ) {
        metrics.recordFallback();

        log.warn(
                "Bedrock circuit-breaker fallback activated reason={}",
                throwable.getMessage()
        );

        return """
                ## Executive Summary

                AI-generated analysis is temporarily unavailable.

                ## Probable Root Cause

                Refer to the deterministic root-cause analysis produced by PulseOps.

                ## Failure Chain

                Refer to the correlated telemetry evidence and deterministic investigation timeline.

                ## Business Impact

                Automated AI impact assessment is temporarily unavailable.

                ## Recommended Actions

                Review the deterministic evidence, highest-ranked root-cause service, and correlated failure timeline.

                ## Confidence and Caveats

                This is a resilience fallback response. No LLM inference was performed.
                """;
    }
}