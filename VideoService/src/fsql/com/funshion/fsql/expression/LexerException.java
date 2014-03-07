package com.funshion.fsql.expression;


/**
 * An exception that occured in the Lexer stage
 *
 */
public class LexerException extends RuntimeException {
    private static final long serialVersionUID = -6905527358249165699L;

    public LexerException(String message, int position) {
        super(message + " @position " + position);
    }
}
