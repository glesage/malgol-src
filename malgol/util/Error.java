package malgol.util;

import malgol.ast.ASTNode;

public class Error {
    public static void msg(String s)
    {
        System.err.println(s);
        System.exit(1);
    }
    
    public static void msg(String s, ASTNode n) {
    	System.err.println(s);
    	System.err.println(n.getPositionString());
    	System.err.println(n);
    	System.exit(1);
    }

}