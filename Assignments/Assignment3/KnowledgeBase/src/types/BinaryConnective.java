package types;

public class BinaryConnective {
    public final static String AND = "^";
    public final static String OR = "v";
    public final static String IF = "=>";
    public final static String IFF = "<=>";
    
    public String val;
    
    public BinaryConnective(String s) { 
        val = s; 
    }

    public boolean is( String type ) {
    	return type.equals( val );
    }
    
    public String toString() { 
        return val; 
    }
    
    public boolean equals(Object obj) { 
        return val.equals(obj);
    }
    
    public boolean AndOp() { 
        return val.equals(AND); 
    }

    public boolean OrOp() {
        return val.equals(OR);
    }

    public boolean IfOp() { 
        return val.equals(IF);
    }

    public boolean IffOp() { 
        return val.equals(IFF);
    }
    
}
