/**
 * Centralizes custom Micrometer metrics used to monitor
 * AI requests, incident processing, and application health.
 */

package com.mansi.pulseops.observability.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PulseOpsMetrics {

    private final Counter aiRequests;
    private final Counter aiSuccesses;
    private final Counter aiFailures;
    private final Counter aiFallbacks;
    private final Timer aiLatency;

    public PulseOpsMetrics(
            MeterRegistry registry
    ) {
        this.aiRequests = Counter.builder(
                        "pulseops.ai.requests"
                )
                .description(
                        "Total AI analysis requests"
                )
                .register(registry);

        this.aiSuccesses = Counter.builder(
                        "pulseops.ai.successes"
                )
                .description(
                        "Successful AI analyses"
                )
                .register(registry);

        this.aiFailures = Counter.builder(
                        "pulseops.ai.failures"
                )
                .description(
                        "Failed AI analyses"
                )
                .register(registry);

        this.aiFallbacks = Counter.builder(
                        "pulseops.ai.fallbacks"
                )
                .description(
                        "AI fallback responses"
                )
                .register(registry);

        this.aiLatency = Timer.builder(
                        "pulseops.ai.latency"
                )
                .description(
                        "AI provider latency"
                )
                .publishPercentileHistogram()
                .register(registry);
    }

    public void recordRequest() {
        aiRequests.increment();
    }

    public void recordSuccess() {
        aiSuccesses.increment();
    }

    public void recordFailure() {
        aiFailures.increment();
    }

    public void recordFallback() {
        aiFallbacks.increment();
    }

    public void recordLatency(
            long durationNanos
    ) {
        aiLatency.record(
                durationNanos,
                TimeUnit.NANOSECONDS
        );
    }
}