package com.github.frosxt.jsonconfig.reader;

import com.github.frosxt.jsonconfig.api.JsonOptions;
import com.github.frosxt.jsonconfig.api.JsonParseOptions;
import com.github.frosxt.jsonconfig.api.exception.JsonConfigException;
import com.github.frosxt.jsonconfig.api.exception.JsonParseException;
import com.github.frosxt.jsonconfig.reader.lex.JsonLexer;
import com.github.frosxt.jsonconfig.reader.lex.TokenType;
import com.github.frosxt.jsonconfig.tree.JsonNode;
import com.github.frosxt.jsonconfig.tree.container.JsonArray;
import com.github.frosxt.jsonconfig.tree.container.JsonObject;
import com.github.frosxt.jsonconfig.tree.scalar.JsonBoolean;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNull;
import com.github.frosxt.jsonconfig.tree.scalar.JsonNumber;
import com.github.frosxt.jsonconfig.tree.scalar.JsonString;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonParser {
    private final JsonLexer lexer;
    private final JsonOptions.DuplicateKeyPolicy duplicateKeyPolicy;
    private final JsonOptions.NumberMode numberMode;
    private TokenType currentToken;

    public JsonParser(final Reader reader, final JsonOptions options) throws IOException {
        this.lexer = new JsonLexer(reader);
        this.duplicateKeyPolicy = options != null ? options.duplicateKeyPolicy() : JsonOptions.DuplicateKeyPolicy.ERROR;
        this.numberMode = options != null ? options.numberMode() : JsonOptions.NumberMode.EXACT;
        this.currentToken = lexer.nextToken();
    }

    public JsonParser(final Reader reader, final JsonParseOptions parseOptions) throws IOException {
        this.lexer = new JsonLexer(reader);
        this.duplicateKeyPolicy = parseOptions != null ? parseOptions.duplicateKeyPolicy() : JsonOptions.DuplicateKeyPolicy.ERROR;
        this.numberMode = parseOptions != null ? parseOptions.numberMode() : JsonOptions.NumberMode.EXACT;
        this.currentToken = lexer.nextToken();
    }

    public JsonNode parse() throws IOException {
        final JsonNode result = parseValue();
        if (currentToken != TokenType.EOF) {
            throw new JsonParseException("Expected EOF", lexer.getLine(), lexer.getColumn());
        }
        return result;
    }

    private JsonNode parseValue() throws IOException {
        switch (currentToken) {
            case START_OBJECT:
                return parseObject();
            case START_ARRAY:
                return parseArray();
            case STRING:
                final JsonString string = new JsonString(lexer.getTokenValue());
                consume(TokenType.STRING);
                return string;
            case NUMBER:
                final JsonNumber number = parseNumber(lexer.getTokenValue());
                consume(TokenType.NUMBER);
                return number;
            case TRUE:
                consume(TokenType.TRUE);
                return JsonBoolean.TRUE;
            case FALSE:
                consume(TokenType.FALSE);
                return JsonBoolean.FALSE;
            case NULL:
                consume(TokenType.NULL);
                return JsonNull.INSTANCE;
            default:
                throw new JsonParseException("Unexpected token: " + currentToken, lexer.getLine(), lexer.getColumn());
        }
    }

    private JsonObject parseObject() throws IOException {
        final JsonObject object = new JsonObject();
        consume(TokenType.START_OBJECT);

        while (currentToken != TokenType.END_OBJECT) {
            if (currentToken != TokenType.STRING) {
                throw new JsonParseException("Expected string key", lexer.getLine(), lexer.getColumn());
            }
            final String key = lexer.getTokenValue();
            consume(TokenType.STRING);

            consume(TokenType.COLON);

            final JsonNode value = parseValue();

            if (object.has(key)) {
                handleDuplicateKey(object, key, value);
            } else {
                object.add(key, value);
            }

            if (currentToken == TokenType.COMMA) {
                consume(TokenType.COMMA);
                if (currentToken == TokenType.END_OBJECT) {
                    throw new JsonParseException("Trailing comma not allowed", lexer.getLine(), lexer.getColumn());
                }
            } else {
                break;
            }
        }

        consume(TokenType.END_OBJECT);
        return object;
    }

    private void handleDuplicateKey(final JsonObject object, final String key, final JsonNode newValue) {
        switch (duplicateKeyPolicy) {
            case ERROR:
                throw new JsonParseException("Duplicate key: " + key, lexer.getLine(), lexer.getColumn());
            case LAST_WINS:
                object.add(key, newValue);
                break;
            case FIRST_WINS:
                break;
            case COLLECT:
                final JsonNode existing = object.get(key);
                final JsonArray array;

                if (existing instanceof final JsonArray jsonArray) {
                    array = jsonArray;
                } else {
                    array = new JsonArray();
                    array.add(existing);
                    object.add(key, array);
                }

                array.add(newValue);
                break;
        }
    }

    private JsonArray parseArray() throws IOException {
        final JsonArray array = new JsonArray();
        consume(TokenType.START_ARRAY);

        while (currentToken != TokenType.END_ARRAY) {
            final JsonNode value = parseValue();
            array.add(value);

            if (currentToken == TokenType.COMMA) {
                consume(TokenType.COMMA);
                if (currentToken == TokenType.END_ARRAY) {
                    throw new JsonParseException("Trailing comma not allowed", lexer.getLine(), lexer.getColumn());
                }
            } else {
                break;
            }
        }

        consume(TokenType.END_ARRAY);
        return array;
    }

    private JsonNumber parseNumber(final String value) {
        if (numberMode == JsonOptions.NumberMode.FAST_DOUBLE) {
            return new JsonNumber(Double.parseDouble(value));
        } else {
            try {
                if (value.indexOf('.') == -1 && value.indexOf('e') == -1 && value.indexOf('E') == -1) {
                    return new JsonNumber(new BigInteger(value));
                } else {
                    return new JsonNumber(new BigDecimal(value));
                }
            } catch (final NumberFormatException e) {
                throw new JsonConfigException("Invalid number: " + value, e);
            }
        }
    }

    private void consume(final TokenType expected) throws IOException {
        if (currentToken == expected) {
            currentToken = lexer.nextToken();
        } else {
            throw new JsonParseException("Expected " + expected + " but found " + currentToken, lexer.getLine(),
                    lexer.getColumn());
        }
    }
}
