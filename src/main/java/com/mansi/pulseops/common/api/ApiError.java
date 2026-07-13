/**
 * Standard error response returned by REST endpoints
 * for client and server-side failures.
 */

package com.mansi.pulseops.common.api;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(OffsetDateTime timestamp, int status, String error, String message, String path,
                       Map<String, String> validationErrors) {
}
