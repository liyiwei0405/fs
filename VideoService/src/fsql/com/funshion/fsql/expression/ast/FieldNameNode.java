package com.funshion.fsql.expression.ast;

public class FieldNameNode{
	public int position;
	public final String name;
	public FieldNameNode(int position, String name) {
		this.position = position;
		this.name = name;
	}
}
