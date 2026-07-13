/**
 * Entry point for the PulseOps AI platform.
 *
 * <p>Bootstraps the Spring Boot application and initializes
 * telemetry ingestion, incident correlation, investigation,
 * and AI-assisted root cause analysis components.</p>
 */

package com.mansi.pulseops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PulseOpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PulseOpsApplication.class, args);
    }
}
