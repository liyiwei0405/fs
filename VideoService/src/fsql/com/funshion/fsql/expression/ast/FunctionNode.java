//package com.funshion.fsql.expression.ast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.funshion.fsql.expression.Interpreter;
//
//
///**
// * Function declaration.
// */
//public class FunctionNode extends Node {
//    final static public List<Node> NO_PARAMETERS = new ArrayList<Node>(0);
//
//    private List<Node> parameters;
//    private Node body;
//
//    public FunctionNode(int pos, List<Node> parameters, Node body) {
//        super(pos);
//        this.parameters = parameters;
//        this.body = body;
//    }
//
//    @Override
//    public Object eval(Interpreter interpreter) throws Exception {
//    	return null;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("(function (");
//        boolean first = true;
//        for (Node node : parameters) {
//            if (first) {
//                first = false;
//            } else {
//                sb.append(' ');
//            }
//            sb.append(node);
//        }
//        sb.append(") ");
//        sb.append(body);
//        sb.append(')');
//        return sb.toString();
//    }
//
//}
