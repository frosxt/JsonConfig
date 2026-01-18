package com.github.frosxt.jsonconfig.api.exception;

public class JsonParseException extends JsonConfigException {
    private final int line;
    private final int column;
    private final int offset;

    /**
     * Creates a new parse exception.
     * 
     * @param message error message
     * @param line    line number
     * @param column  column number
     */
    public JsonParseException(final String message, final int line, final int column) {
        this(message, line, column, -1);
    }

    /**
     * Creates a new parse exception with offset.
     * 
     * @param message error message
     * @param line    line number
     * @param column  column number
     * @param offset  character offset
     */
    public JsonParseException(final String message, final int line, final int column, final int offset) {
        super(formatMessage(message, line, column, offset));
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    /**
     * Creates a new parse exception with cause.
     * 
     * @param message error message
     * @param line    line number
     * @param column  column number
     * @param cause   cause
     */
    public JsonParseException(final String message, final int line, final int column, final Throwable cause) {
        this(message, line, column, -1, cause);
    }

    /**
     * Creates a new parse exception with offset and cause.
     * 
     * @param message error message
     * @param line    line number
     * @param column  column number
     * @param offset  character offset
     * @param cause   cause
     */
    public JsonParseException(final String message, final int line, final int column, final int offset,
            final Throwable cause) {
        super(formatMessage(message, line, column, offset), cause);
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    private static String formatMessage(final String message, final int line, final int column, final int offset) {
        final StringBuilder sb = new StringBuilder(message);
        sb.append(" at line ").append(line).append(", column ").append(column);
        if (offset >= 0) {
            sb.append(" (offset ").append(offset).append(")");
        }

        return sb.toString();
    }

    /**
     * Gets the line number where the error occurred.
     * 
     * @return line number
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where the error occurred.
     * 
     * @return column number
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the character offset where the error occurred.
     * 
     * @return offset or -1 if unknown
     */
    public int getOffset() {
        return offset;
    }
}
