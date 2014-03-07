package com.funshion.ucs.exceptions;
public class ErrorIpFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public ErrorIpFormatException(String str) {
		super("error ip segment '" + str + "'");
	}
}