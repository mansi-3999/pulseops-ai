/**
 * Abstraction for AI providers capable of generating
 * incident investigation reports.
 */

package com.mansi.pulseops.ai.client;

public interface AiClient {

    String analyze(String prompt);
}