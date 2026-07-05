package com.mansi.pulseops.correlation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pulseops.correlation")
public class CorrelationProperties {

    private int minimumEvents = 2;

    public int getMinimumEvents() {
        return minimumEvents;
    }

    public void setMinimumEvents(int minimumEvents) {
        this.minimumEvents = minimumEvents;
    }
}