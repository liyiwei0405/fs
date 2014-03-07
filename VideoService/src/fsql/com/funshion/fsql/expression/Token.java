package com.funshion.fsql.expression;

/**
 * A <a href="http://en.wikipedia.org/wiki/Lexical_analysis#Token">token</a>
 * is a categorized block of text that represents an atomic element in the source code.
 *
 */
public class Token {
    private final int position;
    public final TokenType type;
    public final String text;

    public Token(int position, TokenType type, String text) {
        this.position = position;
        this.type = type;
        this.text = text;
    }




    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Token))
            return false;
        Token other = (Token) obj;
        return this.type == other.type && this.text.equals(other.text) && this.getPosition() == other.getPosition();
    }

    @Override
    public String toString() {
        return type + ",'" + text + "'";
    }


	public int getPosition() {
		return position;
	}
}
