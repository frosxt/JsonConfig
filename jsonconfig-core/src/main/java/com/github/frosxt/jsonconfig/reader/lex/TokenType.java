package com.github.frosxt.jsonconfig.reader.lex;

public enum TokenType {
    START_OBJECT, // {
    END_OBJECT, // }
    START_ARRAY, // [
    END_ARRAY, // ]
    COLON, // :
    COMMA, // ,
    STRING, // "..."
    NUMBER, // 123.45
    TRUE, // true
    FALSE, // false
    NULL, // null
    EOF // End of input
}
