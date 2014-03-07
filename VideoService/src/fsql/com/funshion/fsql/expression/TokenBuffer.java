package com.funshion.fsql.expression;

import java.util.LinkedList;

/**
 * Buffer of Tokens. Used to provide lookahead into the stream from the lexer.
 * Also filters out comment tokens.
 *
 */
public class TokenBuffer {
    private LinkedList<Token> tokenQueue;
    private Lexer lexer;

    public TokenBuffer(Lexer lexer, int size) {
        this.lexer = lexer;
        tokenQueue = new LinkedList<Token>();

        // init queue
        for (int i = 0; i < size; i++) {
            Token token = nextToken();
            if (token == null) {
                break;
            }
            tokenQueue.addLast(token);
        }
    }

    private Token nextToken() {
      return lexer.getNextToken();
    }

    public boolean isEmpty() {
        return tokenQueue.isEmpty();
    }

    public int size() {
        return tokenQueue.size();
    }

    public Token getToken(int i) {
        return tokenQueue.get(i);
    }

    /**
     * Read the next token from the lexer
     */
    public Token readToken() {
        if (tokenQueue.isEmpty()) {
            return null;
        }
        Token token = tokenQueue.removeFirst();

        // Add another token to the queue
        Token newToken = nextToken();
        if (newToken != null) {
            tokenQueue.addLast(newToken);
        }
        return token;
    }
}
