package com.github.frosxt.jsonconfig.api.exception;

public class JsonConfigException extends RuntimeException {
    /**
     * Creates a new exception with a message.
     * 
     * @param message error message
     */
    public JsonConfigException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with a message and cause.
     * 
     * @param message error message
     * @param cause   cause
     */
    public JsonConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
