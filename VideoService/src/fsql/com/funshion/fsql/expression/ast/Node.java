package com.funshion.fsql.expression.ast;

/**
 * Node in the abstract syntax tree.
 */
public abstract class Node {
    private int position;

    public Node(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
