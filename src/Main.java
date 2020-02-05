import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
public static void main(String[] args) throws FileNotFoundException {
	ProblemState PS = new ProblemState();
    boolean linear = false;
    boolean debug = false;
    boolean complete = false;
    boolean clean = false;
    if(args.length==1)  PS = LoadFile.loadFile(new FileInputStream(new File(args[0])),args[0]);
    else if(args.length==2) {
    	PS = LoadFile.loadFile(new FileInputStream(new File(args[0])),args[0]);
    	linear=(args[1].compareTo("linear")== 0)? true: false;
    	debug=( args[1].compareTo("debug")== 0)? true: false;
    	clean=( args[1].compareTo("clean")== 0)? true: false;
    }
    else if(args.length==3) {
    	PS = LoadFile.loadFile(new FileInputStream(new File(args[0])),args[0]);
    	linear=( args[1].compareTo("linear")== 0)? true: false;
    	 debug=( args[1].compareTo("debug")== 0)? true: false;
    	 clean=( args[1].compareTo("clean")== 0)? true: false;
    	 if(linear) {
    		 clean=( args[2].compareTo("clean")== 0)? true: false;
    		 debug=( args[2].compareTo("debug")== 0)? true: false;
    	 }
    	 else if(debug) {
    		 complete=( args[2].compareTo("complete")== 0)? true: false;
         	 clean=( args[2].compareTo("clean")== 0)? true: false;
    	 }
    	 else {
    			System.out.println("illegal args");
    	    	return;
    	 }
    }
    else if(args.length==4) {
    	PS = LoadFile.loadFile(new FileInputStream(new File(args[0])),args[0]);
    	linear=( args[1].compareTo("linear")== 0)? true: false;
    	 debug=( args[1].compareTo("debug")== 0)? true: false;
     	 clean=( args[1].compareTo("clean")== 0)? true: false;
    	 if(linear) {
    		 clean=( args[2].compareTo("clean")== 0)? true: false;
    		 debug=( args[2].compareTo("debug")== 0)? true: false;
    		 if(debug) {
        		 clean=( args[3].compareTo("clean")== 0)? true: false;
        		 complete=( args[3].compareTo("complete")== 0)? true: false;
    		 }
    	 }
    	 else if(debug) {
    		 complete=( args[2].compareTo("complete")== 0)? true: false;
         	 clean=( args[2].compareTo("clean")== 0)? true: false;
    		 if(complete) clean=( args[3].compareTo("clean")== 0)? true: false;
    	 }
    	 else {
 			System.out.println("illegal args");
 	    	return;
    	 }
    }
    else if(args.length==5) {
    	PS = LoadFile.loadFile(new FileInputStream(new File(args[0])),args[0]);
    	linear=( args[1].compareTo("linear")== 0)? true: false;
    	debug=( args[2].compareTo("debug")== 0)? true: false;
   	 	complete=( args[3].compareTo("complete")== 0)? true: false;
    	 clean=( args[4].compareTo("clean")== 0)? true: false;
    }
    else {
    	System.out.println("illegal args");
    	return;
    }
    PS.Bindings.add(new Binding((Const) PS.problems.get(0).Variable.Dup(),PS.problems.get(0).Variable.Dup()));
   


	System.out.println();
	System.out.println("Problem: "+PS.problems.get(0).toString());
	GenRules.debugFunc("Initial State",PS, debug, complete);
	if(linear || PS.UnitFunc.size()==0) algorLinear(PS,debug,complete);
	else if(PS.UnitFunc.size()==1) algorSingle(PS,debug,complete);
	else if(PS.UnitFunc.size()>=1) algorgen(PS,debug,complete);
	System.out.println("# of Rules Applied: "+PS.counter);
	System.out.println("Solution: ");
	System.out.println("*********************************************");
	System.out.println();
	if(clean) cleagrammar(PS);
	System.out.print(PS.toString());
}



private static void algorgen(ProblemState PS, boolean debug, boolean complete) {

	while(PS.problems.size()!=0) {
		boolean state = true;
    	if(PS.UnitFunc.size()!=0 &&  PS.checkLoop(0)==-1 && !PS.IsUnitFunc(PS.problems.get(0).Left.getSym()) && !PS.IsUnitFunc(PS.problems.get(0).Right.getSym()) 
    			&&     	   PS.problems.get(0).Left.depth()+PS.problems.get(0).Right.depth()== 2) {
    		 state = GenRules.StartLoop(0, PS,debug,complete);
    		 int cursize = PS.problems.size();
    		 GenRules.decompose(cursize-1, PS,debug,complete);
    		 GenRules.decompose(cursize-2, PS,debug,complete);
    	     GenRules.SatLoop(PS,debug,complete);
 		   	 PS.cleanBindings();
    	}
    	if(PS.UnitFunc.size()!=0 &&  PS.checkLoop(0)!=-1 && !PS.IsUnitFunc(PS.problems.get(0).Left.getSym()) && !PS.IsUnitFunc(PS.problems.get(0).Right.getSym()) 
    			&&    	   PS.problems.get(0).Left.depth()+PS.problems.get(0).Right.depth()== 2) 
    		while(state) {
    			state = GenRules.BranchLoop(0, PS,debug,complete);
    			if(state) {
    				int cursize = PS.problems.size();
    				GenRules.decompose(cursize-1, PS,debug,complete);
        			GenRules.decompose(cursize-2, PS,debug,complete);
    			}
    			GenRules.SatLoop(PS,debug,complete);
    			PS.cleanBindings();
    		}
		state = GenRules.decompose(0, PS,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		if(!state) state = GenRules.UnitDecompose(0, PS,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		if(!state) GenRules.Solve(0, PS,false,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		PS.cleanBindings();
	}	
}

private static void algorSingle(ProblemState PS,boolean debug,boolean complete) {
	while(PS.problems.size()!=0) {
		boolean state = true;
    	if(PS.checkLoop(0)==-1 && 
    	  (PS.problems.get(0).Left.getSym().compareTo(PS.problems.get(0).Right.getSym())!=0 || PS.problems.get(0).Left.getSym().compareTo("U_f")!=0) &&  
    	   PS.UnitFunc.size()==1 && !PS.IsUnitFunc(PS.problems.get(0).Left.getSym()) && !PS.IsUnitFunc(PS.problems.get(0).Right.getSym())) {
			state = GenRules.StartLoop(0, PS,debug,complete);
			int cursize = PS.problems.size();
			GenRules.decompose(cursize-1, PS,debug,complete);
			GenRules.decompose(cursize-2, PS,debug,complete);
    	}
		GenRules.SatLoop(PS,debug,complete);
		state = GenRules.decompose(0, PS,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		if(!state) state = GenRules.UnitDecompose(0, PS,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		if(!state) GenRules.Solve(0, PS,false,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		PS.cleanBindings();
	}	
}

private static void algorLinear(ProblemState PS,boolean debug, boolean complete) {
	while(PS.problems.size()!=0) {
		boolean state = true;
		state = GenRules.decompose(0, PS,debug,complete);
		if(!state) state = GenRules.UnitDecompose(0, PS,debug,complete);
		if(!state) GenRules.Solve(0, PS,false,debug,complete);
		GenRules.SatLoop(PS,debug,complete);
		PS.cleanBindings();
	}		
}
private static void cleagrammar(ProblemState ps) {
	HashMap<Const,HashSet<Term>> bindingMap = new HashMap<Const,HashSet<Term>>();
	for(Binding b: ps.Bindings) {
		HashSet<Term> terms = new HashSet<Term>();
		if(bindingMap.containsKey(b.NonTerm)) {
			terms = bindingMap.get(b.NonTerm);
			terms.add(b.Right);
			bindingMap.put(b.NonTerm, terms);
		}else {
			terms.add(b.Right);
			bindingMap.put(b.NonTerm, terms);
		}
	}
	HashSet<Const> singletonOccur = new HashSet<Const>();
	for(Const c: bindingMap.keySet()) if(OnlySingleOccur(c,bindingMap)) singletonOccur.add(c);
	
	HashSet<Const> actuallyoccurs = new HashSet<Const>();
	for(Const c: singletonOccur) if(actuallyoccurs(c,bindingMap)) actuallyoccurs.add(c);
	
	for(Const c: actuallyoccurs) {	
	    HashMap<Const,HashSet<Term>> NewbindingMap = new HashMap<Const,HashSet<Term>>();
		HashSet<Term> trans = bindingMap.remove(c);
		for(Const cc: bindingMap.keySet()) {
			HashSet<Term> newforcc = new HashSet<Term>();
			for(Term t: bindingMap.get(cc)) 
				if(t.getSym().compareTo(c.getSym())==0) newforcc.addAll(trans);
				else newforcc.add(t);
			NewbindingMap.put(cc, newforcc);
		}	
		bindingMap = NewbindingMap;
	}	
	HashSet<Const> NonSelfLoops = new HashSet<Const>();
	for(Const c: bindingMap.keySet()) 
		if(NonSelfLoops(c,bindingMap)) 
			NonSelfLoops.add(c);
    actuallyoccurs = new HashSet<Const>();
	for(Const c: NonSelfLoops) if(actuallyoccurs(c,bindingMap)) actuallyoccurs.add(c);
	
	for(Const c: actuallyoccurs) {	
	    HashMap<Const,HashSet<Term>> NewbindingMap = new HashMap<Const,HashSet<Term>>();
		HashSet<Term> trans = bindingMap.remove(c);
		for(Const cc: bindingMap.keySet()) {
			HashSet<Term> newforcc = new HashSet<Term>();
			for(Term t: bindingMap.get(cc)) 
				if(t.contains(c)) 
					for(Term ct : trans) 
						newforcc.add(t.replace(c,ct));
				else newforcc.add(t);
			NewbindingMap.put(cc, newforcc);
		}	
		bindingMap = NewbindingMap;
	}

	ps.Bindings = new HashSet<Binding>();
	for(Const c: bindingMap.keySet()) {
		for(Term t: bindingMap.get(c)) 
			ps.Bindings.add(new Binding(c,t));
	}
	ps.cleanBindings();
}


private static boolean actuallyoccurs(Const ch, HashMap<Const, HashSet<Term>> bindingMap) {
	boolean ret = false;
	for(Const c: bindingMap.keySet()) 
		for(Term t: bindingMap.get(c)) if(t.contains(ch)) ret = true;		
	return ret;
}



private static boolean NonSelfLoops(Const ch, HashMap<Const, HashSet<Term>> bindingMap) {
	boolean ret = true;
	for(Const c: bindingMap.keySet()) 
		for(Term t: bindingMap.get(c)) 
			if(t.contains(ch) && c.getSym().compareTo(ch.getSym())==0) ret = false;				
	return ret;
}



private static boolean OnlySingleOccur(Const ch, HashMap<Const, HashSet<Term>> bindingMap) {
	boolean ret = true;
	for(Const c: bindingMap.keySet()) 
		for(Term t: bindingMap.get(c)) 
			if(t.contains(ch) && !(t instanceof Const)) ret = false;		
	return ret;
}
}
