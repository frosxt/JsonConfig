package com.github.frosxt.jsonconfig.reader.lex;

import com.github.frosxt.jsonconfig.api.exception.JsonParseException;

import java.io.IOException;
import java.io.Reader;

public class JsonLexer {
    private static final int BUFFER_SIZE = 8192;
    private final Reader reader;
    private final char[] buffer = new char[BUFFER_SIZE];
    private int bufferPos = 0;
    private int bufferLimit = 0;
    private int current;
    private int line = 1;
    private int column = 0;
    private int offset = 0;
    private final StringBuilder stringBuffer = new StringBuilder();

    private String tokenValue;

    public JsonLexer(final Reader reader) throws IOException {
        this.reader = reader;
        read();
    }

    private void read() throws IOException {
        if (bufferLimit == -1) {
            current = -1;
            return;
        }

        if (bufferPos >= bufferLimit) {
            fillBuffer();
            if (bufferLimit == -1) {
                current = -1;
                return;
            }
        }

        current = buffer[bufferPos++];
        offset++;

        if (current == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
    }

    private void fillBuffer() throws IOException {
        bufferLimit = reader.read(buffer, 0, BUFFER_SIZE);
        bufferPos = 0;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getOffset() {
        return offset - 1;
    }

    public TokenType nextToken() throws IOException {
        skipWhitespace();

        if (current == -1) {
            return TokenType.EOF;
        }

        return switch (current) {
            case '{' -> {
                read();
                yield TokenType.START_OBJECT;
            }
            case '}' -> {
                read();
                yield TokenType.END_OBJECT;
            }
            case '[' -> {
                read();
                yield TokenType.START_ARRAY;
            }
            case ']' -> {
                read();
                yield TokenType.END_ARRAY;
            }
            case ':' -> {
                read();
                yield TokenType.COLON;
            }
            case ',' -> {
                read();
                yield TokenType.COMMA;
            }
            case '"' -> readString();
            case 't' -> readTrue();
            case 'f' -> readFalse();
            case 'n' -> readNull();
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> readNumber();
            default ->
                    throw new JsonParseException("Unexpected character: " + (char) current, line, column, getOffset());
        };
    }

    private void skipWhitespace() throws IOException {
        while (current != -1 && Character.isWhitespace(current)) {
            read();
        }
    }

    private TokenType readString() throws IOException {
        stringBuffer.setLength(0);
        read();

        while (current != -1) {
            if (current == '"') {
                read();
                tokenValue = stringBuffer.toString();
                return TokenType.STRING;
            } else if (current == '\\') {
                read();
                if (current == -1) {
                    throw new JsonParseException("Unexpected EOF in string", line, column, getOffset());
                }
                switch (current) {
                    case '"':
                        stringBuffer.append('"');
                        break;
                    case '\\':
                        stringBuffer.append('\\');
                        break;
                    case '/':
                        stringBuffer.append('/');
                        break;
                    case 'b':
                        stringBuffer.append('\b');
                        break;
                    case 'f':
                        stringBuffer.append('\f');
                        break;
                    case 'n':
                        stringBuffer.append('\n');
                        break;
                    case 'r':
                        stringBuffer.append('\r');
                        break;
                    case 't':
                        stringBuffer.append('\t');
                        break;
                    case 'u':
                        stringBuffer.append(readUnicode());
                        break;
                    default:
                        throw new JsonParseException("Invalid escape sequence: \\" + (char) current, line, column, getOffset());
                }
                read();
            } else {
                if (current < 0x20) {
                    throw new JsonParseException("Illegal unescaped control character: " + String.format("0x%02x", current), line, column, getOffset());
                }
                stringBuffer.append((char) current);
                read();
            }
        }

        throw new JsonParseException("Unterminated string", line, column, getOffset());
    }

    private char readUnicode() throws IOException {
        int val = 0;
        for (int i = 0; i < 4; i++) {
            read();
            if (current == -1) {
                throw new JsonParseException("Unexpected EOF in unicode escape", line, column, getOffset());
            }
            final int digit = Character.digit(current, 16);
            if (digit == -1) {
                throw new JsonParseException("Invalid unicode escape character: " + (char) current, line, column,
                        getOffset());
            }
            val = (val << 4) | digit;
        }

        return (char) val;
    }

    private TokenType readTrue() throws IOException {
        expect("true");
        return TokenType.TRUE;
    }

    private TokenType readFalse() throws IOException {
        expect("false");
        return TokenType.FALSE;
    }

    private TokenType readNull() throws IOException {
        expect("null");
        return TokenType.NULL;
    }

    private void expect(final String expected) throws IOException {
        for (int i = 0; i < expected.length(); i++) {
            if (current != expected.charAt(i)) {
                throw new JsonParseException("Expected '" + expected + "'", line, column, getOffset());
            }
            read();
        }
    }

    private TokenType readNumber() throws IOException {
        stringBuffer.setLength(0);
        if (current == '-') {
            stringBuffer.append('-');
            read();
        }

        if (current == '0') {
            stringBuffer.append('0');
            read();
            if (Character.isDigit(current)) {
                throw new JsonParseException("Leading zeros are not allowed", line, column, getOffset());
            }
        } else if (Character.isDigit(current)) {
            while (Character.isDigit(current)) {
                stringBuffer.append((char) current);
                read();
            }
        } else {
            throw new JsonParseException("Invalid number format", line, column, getOffset());
        }

        if (current == '.') {
            stringBuffer.append('.');
            read();
            if (!Character.isDigit(current)) {
                throw new JsonParseException("Invalid fraction part", line, column, getOffset());
            }
            while (Character.isDigit(current)) {
                stringBuffer.append((char) current);
                read();
            }
        }

        if (current == 'e' || current == 'E') {
            stringBuffer.append((char) current);
            read();
            if (current == '+' || current == '-') {
                stringBuffer.append((char) current);
                read();
            }
            if (!Character.isDigit(current)) {
                throw new JsonParseException("Invalid exponent part", line, column, getOffset());
            }
            while (Character.isDigit(current)) {
                stringBuffer.append((char) current);
                read();
            }
        }

        tokenValue = stringBuffer.toString();
        return TokenType.NUMBER;
    }
}
