public class FunctionDefinition {
    String name;
    Integer arity;
    Boolean unity;

    public FunctionDefinition(String s, int a, boolean u) {
        name = s;
        arity = a;
        unity = u;
    }
    public String toString(){
    	return name+" : "+arity.toString()+" : "+ ((unity)? "Unit":"Free");
    }
}
