//package com.funshion.fsql.expression.ast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.funshion.fsql.expression.Interpreter;
//
//
///**
// * Call to function.
// */
//public class FunctionCallNode extends Node {
//    final static public List<Node> NO_ARGUMENTS = new ArrayList<Node>(0);
//
//    private String functionName;
//    private List<Node> arguments;
//
//    public FunctionCallNode(int pos, String functionName, List<Node> arguments) {
//        super(pos);
//        this.functionName = functionName;
//        this.arguments = arguments;
//    }
//
//    @Override
//    public Object eval(Interpreter interpreter) throws Exception {
//        interpreter.checkFunctionExists(functionName, getPosition());
//        // Evaluate the arguments
//        List<Object> args = new ArrayList<Object>(arguments.size());
//        for (Node node : arguments) {
//            args.add(node.eval(interpreter));
//        }
//        return interpreter.callFunction(functionName, args, getPosition());
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append('(');
//        sb.append(functionName);
//        for (Node arg : arguments) {
//            sb.append(' ');
//            sb.append(arg);
//        }
//        sb.append(')');
//        return sb.toString();
//    }
//}
