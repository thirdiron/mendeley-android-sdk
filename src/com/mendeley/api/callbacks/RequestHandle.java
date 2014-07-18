package com.mendeley.api.callbacks;

/**
 * Represents a particular invocation of an API call.
 */
public interface RequestHandle {
    /**
     * Cancel the request.
     */
    public void cancel();
}
