package com.funshion.fsql.expression;

import java.io.IOException;

public class PeekReader {
	private final char[] in;
	private int currentPos;
	public PeekReader(String str) throws IOException {
		in = str.toCharArray();
		currentPos = 0;
	}

	public void close() throws IOException {}


	public int read() throws IOException {
		int c = in[currentPos ++];
		return c;
	}

	/**
	 * Return a character that is further in the stream.
	 *
	 * @param lookAhead How far to look into the stream.
	 * @return Character that is lookAhead characters into the stream.
	 */
	public int peek(int lookAhead) {
		if (lookAhead < 1) {
			throw new IndexOutOfBoundsException("lookAhead must be between 1 and " + in.length);
		}
		int to = this.currentPos + lookAhead - 1;
		if (to >= in.length) {
			return -1;
		}
		return in[to];
	}

}
