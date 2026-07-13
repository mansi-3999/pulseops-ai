/**
 * Defines the contract for telemetry correlation strategies
 * used to identify related failures.
 */

package com.mansi.pulseops.correlation.strategy;

import com.mansi.pulseops.correlation.model.CorrelationDecision;
import com.mansi.pulseops.telemetry.domain.TelemetryEvent;

public interface CorrelationStrategy {

    CorrelationDecision evaluate(
            TelemetryEvent event
    );
}