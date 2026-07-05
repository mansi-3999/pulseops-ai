package com.mansi.pulseops.ai.client;

import com.mansi.pulseops.ai.config.AiProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

@Component
public class BedrockAiClient implements AiClient {

    private final BedrockRuntimeClient client;
    private final AiProperties properties;

    public BedrockAiClient(
            BedrockRuntimeClient client,
            AiProperties properties
    ) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public String analyze(String prompt) {

        if (!properties.isEnabled()) {
            throw new IllegalStateException(
                    "AI analysis is disabled. " +
                            "Set PULSEOPS_AI_ENABLED=true."
            );
        }

        Message userMessage = Message.builder()
                .role(ConversationRole.USER)
                .content(
                        ContentBlock.fromText(prompt)
                )
                .build();

        InferenceConfiguration inferenceConfig =
                InferenceConfiguration.builder()
                        .maxTokens(properties.getMaxTokens())
                        .temperature(
                                (float) properties.getTemperature()
                        )
                        .build();

        ConverseRequest request =
                ConverseRequest.builder()
                        .modelId(properties.getModelId())
                        .messages(userMessage)
                        .inferenceConfig(inferenceConfig)
                        .build();

        ConverseResponse response =
                client.converse(request);

        if (response.output() == null
                || response.output().message() == null
                || response.output().message().content().isEmpty()) {
            throw new IllegalStateException(
                    "Bedrock returned an empty response"
            );
        }

        return response.output()
                .message()
                .content()
                .stream()
                .filter(block -> block.text() != null)
                .map(ContentBlock::text)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Bedrock response contained no text"
                        )
                );
    }
}