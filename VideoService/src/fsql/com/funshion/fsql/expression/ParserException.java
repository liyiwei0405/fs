package com.funshion.fsql.expression;


/**
 * An exception that occurred in the parser stage
 *
 */
public class ParserException extends RuntimeException {
    private static final long serialVersionUID = 7505060960165209530L;

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, int position) {
        super(message + " at col " + position);
    }
}
