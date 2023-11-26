package types;

public class BinaryConnective {
    public final static BinaryConnective AND = new BinaryConnective("^");
    public final static BinaryConnective OR = new BinaryConnective("v");
    public final static BinaryConnective IF = new BinaryConnective("=>");
    public final static BinaryConnective IFF = new BinaryConnective("<=>");
    
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
}
