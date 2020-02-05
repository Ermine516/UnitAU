import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ProblemState {
public ArrayList<AUP> problems;
public ArrayList<AUP> Store;
public ArrayList<Loop> Loops;
public HashSet<Binding> Bindings;
public ArrayList<FunctionDefinition> Signature;
public ArrayList<FunctionDefinition> UnitFunc;
private int varcounter;
public int counter = 0;

public ProblemState() {
	varcounter = 0;
    counter = 0;
	problems = new ArrayList<AUP>();
	Store = new ArrayList<AUP>();
	Loops = new ArrayList<Loop>();
	Bindings = new HashSet<Binding>();
	Signature = new ArrayList<FunctionDefinition>();
	UnitFunc = new ArrayList<FunctionDefinition>();
}

public String nVar(String var) {
	varcounter++;
	return var.split("_")[0]+"_"+varcounter;
}
public int checkLoop(int probpos) {
	if(probpos>problems.size()) return -1; 
	AUP a = problems.get(probpos);
	for(int i= 0; i< Loops.size(); i++)
		if(Loops.get(i).LoopAUP.Left.toString().compareTo(a.Left.toString())== 0 && Loops.get(i).LoopAUP.Right.toString().compareTo(a.Right.toString())== 0)
			return i; 
	return -1; 
}


public String toString() {
	/*String ret = "";
	if(this.problems.isEmpty()) ret+="&  \\emptyset ; ";
	else {
		ret+="& \\{";
		for(AUP a : this.problems) ret+=" "+a.Variable.toString()+":"+a.Left.toString()+" \\triangleq "+a.Right.toString()+" , ";	
		ret = ret.substring(0,ret.length()-2)+"\\} ; ";
	}
	if(this.Store.isEmpty()) ret+="\\emptyset ; ";
	else {
		ret+=" \\{";
		for(AUP a : this.Store) ret+=" "+a.Variable.toString()+":"+a.Left.toString()+" \\triangleq "+a.Right.toString()+" , ";
		ret = ret.substring(0,ret.length()-2)+"\\} ; ";
	}
	if(this.Loops.isEmpty()) ret+="\\emptyset ; ";
	else{
		ret+=" \\{";
		for(Loop a : this.Loops) {
			ret+=" ("+a.LoopAUP.Variable.toString()+":"+a.LoopAUP.Left.toString()+" \\triangleq "+a.LoopAUP.Right.toString()+" , \\{";
			for(int i=0; i< a.unit;i++) {
				ret+= "\\unit{"+this.UnitFunc.get(i).name+"}, ";
			}
			ret = ret.substring(0,ret.length()-2)+"\\}) , ";
		}
		ret = ret.substring(0,ret.length()-2)+"\\} ; ";
	}
	if(this.Bindings.isEmpty()) ret+="\\emptyset ";
	else {
		ret+=" \\{";
		for(Binding b : this.Bindings) ret+=" "+b.NonTerm+" \\mapsto "+b.Right.toString()+" , ";
		ret = ret.substring(0,ret.length()-2)+"\\} ";
	}*/
	/*String rerr = "";
	for(AUP a : this.problems) rerr+= (a.Left.depth()+a.Right.depth())+",";
	if(rerr.length()>0)rerr = rerr.substring(0,rerr.length()-1);
	String[] splitter = rerr.split(",");
	int[] intsplit = new int[splitter.length];
	if(rerr!= ""){
		for(int i = 0; i< splitter.length;i++) intsplit[i]= Integer.parseInt(splitter[i]);

		Arrays.sort(intsplit);
		rerr = "";
		for(int i = intsplit.length-1; i>=0;i--) rerr+= intsplit[i]+",";
		if(rerr.length()>0)rerr = rerr.substring(0,rerr.length()-1);
	}
	String ret = "AUPs:"+rerr+"\n";*/
	String ret = "AUPs:"+"\n";
	for(AUP a : this.problems) ret+= a.toString()+"\n";
	ret+="---------------------------------------------\n";
	ret+="Store:\n";
	for(AUP a : this.Store) ret+= a.toString()+"\n";
	ret+="---------------------------------------------\n";
	ret+="Loops:\n";
	for(Loop a : this.Loops) ret+= a.toString()+"\n";
	ret+="---------------------------------------------\n";
	ret+="Bindings:\n";
	HashMap<Const,HashSet<Term>> BindByVar = new HashMap<Const,HashSet<Term>>() ;
	for(Binding b: Bindings)
		if(BindByVar.containsKey(b.NonTerm)) {
			HashSet<Term> Temp = BindByVar.get(b.NonTerm);
			Temp.add(b.Right);
			BindByVar.put(b.NonTerm, Temp);
		}
		else {
			HashSet<Term> Temp =new  HashSet<Term>();
			Temp.add(b.Right);
			BindByVar.put(b.NonTerm,Temp);
		}
	
	for(Const c : BindByVar.keySet()) {
		ret+=c.toString()+":\n";
		for(Term t : BindByVar.get(c)) ret+=("     "+t.toString()+"\n");
	}
	ret+="---------------------------------------------\n";
	ret+="*********************************************\n";
	ret+="---------------------------------------------\n\n";
return ret;
}

public void cleanBindings() {
	HashSet<Binding> b2 = new HashSet<Binding>();
	for(Binding b: Bindings) {
		Binding bb = new Binding(b.NonTerm,b.Right.UnitNorm());
		if(bb.NonTerm.getSym().compareTo(bb.Right.getSym())!=0) b2.add(bb);
	}
	Bindings = b2;	
	
}

public int unitMismatch(int prob,int from){
    int firstmismatch = from;
	for(int i = from; i< UnitFunc.size();i++){
		if(problems.get(prob).Left.getSym().compareTo("U_"+UnitFunc.get(i).name) != 0 || problems.get(prob).Right.getSym().compareTo("U_"+UnitFunc.get(i).name)!= 0) return firstmismatch;
	    firstmismatch++;
	}
	return firstmismatch;
	
}

public boolean IsUnitFunc(String s){
    for(FunctionDefinition fd : this.UnitFunc) 
    	if(s.compareTo(fd.name)==0) return true;
	return false;
}


}
