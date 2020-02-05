import java.util.ArrayList;
import java.util.HashSet;

public class GenRules {

	public static boolean decompose(int index, ProblemState ps, boolean debug, boolean complete) {
		AUP prob = ps.problems.remove(index);
		if(prob.Left.getSym().compareTo(prob.Right.getSym()) == 0 && (prob.Left.subTerms().size() == prob.Right.subTerms().size())){
			if(prob.Left.subTerms().size()==0) {
				HashSet<Binding> b2 = new HashSet<Binding>();
				for(Binding b: ps.Bindings) b2.add(new Binding(b.NonTerm,b.replace(prob.Variable,prob.Left)));
				ps.Bindings = b2;
			}
			else {
				ArrayList<Term> bindargs = new ArrayList<Term>();
				for(int i = 0;i<prob.Left.subTerms().size();i++) {
					bindargs.add(new Const(ps.nVar(prob.Variable.getSym())));
					ps.problems.add(new AUP(((Const)bindargs.get(bindargs.size()-1)),prob.Left.subTerms().get(i), prob.Right.subTerms().get(i)));
				}
				Term sub = new Func(prob.Left.getSym(),bindargs,prob.Left.Unit()) ; 
				HashSet<Binding> b2 = new HashSet<Binding>();
				for(Binding b: ps.Bindings) b2.add(new Binding(b.NonTerm,b.replace(prob.Variable,sub)));
				ps.Bindings = b2;
			}
	        debugFunc("Dec",ps,debug,complete);
			return true;
		}
		else ps.problems.add(index,prob);
		return false;
		
	}
	public static boolean Solve(int index, ProblemState ps,boolean linear,boolean debug, boolean complete) {
		AUP prob = ps.problems.remove(index);
        ps.Store.add(ps.Store.size(),prob);
        debugFunc("Solve",ps,debug,complete);
       if(!linear) Merge(ps.Store.size()-1,  ps,debug,complete);
        return true;
	}
	
	public static boolean Merge(int index, ProblemState ps,boolean debug,boolean complete) {
		if(ps.Store.size()>0){
			AUP prob = ps.Store.remove(index);
			boolean happened= false;
			for(AUP a: ps.Store) 
				if(a.Left.toString().compareTo(prob.Left.toString()) ==0 && a.Right.toString().compareTo(prob.Right.toString()) ==0) {
					HashSet<Binding> b2 = new HashSet<Binding>();
					for(Binding b: ps.Bindings) b2.add(new Binding(b.NonTerm,b.replace(prob.Variable,a.Variable)));
					ps.Bindings = b2;
					happened =true;
			        debugFunc("Merge",ps,debug,complete);
				}
			if(!happened) ps.Store.add(prob);
		}
        return true;
	}
	public static boolean UnitDecompose(int index, ProblemState ps,boolean debug, boolean complete) {
		AUP prob = ps.problems.remove(index);
		if(prob.Left.Unit() && prob.Right.Unit() && prob.Left.getSym().compareTo(prob.Right.getSym())!=0) {
			debugFunc("Expansion for Unit Both",ps,debug,complete);
			 UDHelper1(prob.Left,prob.Right,ps,prob.Variable,debug);
			 int cursize = ps.problems.size();
    		 GenRules.decompose(cursize-1, ps,debug,complete);
    		 GenRules.decompose(cursize-2, ps,debug,complete);
			 UDHelper2(prob.Right,prob.Left,ps,prob.Variable, debug);
			 cursize = ps.problems.size();
    		 GenRules.decompose(cursize-1, ps,debug,complete);
    		 GenRules.decompose(cursize-2, ps,debug,complete);
			return true;
		}
		else if (prob.Left.Unit()) {
	        debugFunc("Expansion for Unit Right",ps,debug,complete);
			 UDHelper1(prob.Left,prob.Right,ps,prob.Variable,debug);
			int cursize = ps.problems.size();
   		 GenRules.decompose(cursize-1, ps,debug,complete);
   		 GenRules.decompose(cursize-2, ps,debug,complete);
   		return true;
		}
		else if (prob.Right.Unit()) {
	        debugFunc("Expansion for Unit Left",ps,debug,complete);
			 UDHelper2(prob.Right,prob.Left,ps,prob.Variable,debug);
			 int cursize = ps.problems.size();
	   		 GenRules.decompose(cursize-1, ps,debug,complete);
	   		 GenRules.decompose(cursize-2, ps,debug,complete);
	   		 return true;
		}
		else ps.problems.add(index, prob);
		return false;
	}

	public static boolean StartLoop(int index, ProblemState ps,boolean debug, boolean complete ) {
		int unitval = ps.unitMismatch(index,0);
		if(unitval <ps.UnitFunc.size()) {
			AUP prob = ps.problems.remove(index);
			ps.Loops.add(new Loop(prob,unitval+1));
			FunctionDefinition fd = ps.UnitFunc.get(unitval);
			ps.problems.add(index,new AUP(new Const(ps.nVar(prob.Variable.getSym())),prob.Left.Dup(),prob.Right.Dup()));
    		probConstruct(prob,ps,fd);
	        debugFunc("Start Loop Unit",ps,debug,complete);
			return true;
		}
		else return false;
	}
	public static boolean BranchLoop(int index, ProblemState ps,boolean debug, boolean complete) {
		Loop theLoop = ps.Loops.remove(ps.checkLoop(index));
		theLoop.unit= ps.unitMismatch(index,theLoop.unit);
		if(theLoop.unit<ps.UnitFunc.size()) {
    		FunctionDefinition fd = ps.UnitFunc.get(theLoop.unit);
    		ps.Loops.add(new Loop(theLoop.LoopAUP,theLoop.unit+1));
    		AUP prob = ps.problems.remove(index);
    		ps.problems.add(index,new AUP(new Const(ps.nVar(prob.Variable.getSym())),prob.Left.Dup(),prob.Right.Dup()));
    		probConstruct(prob,ps,fd);
	        debugFunc("Branch Loop Unit",ps,debug,complete);
    		return true;
    	}
    	else ps.Loops.add(theLoop);
		return false;
	}
	
	public static boolean SatLoop(ProblemState ps,boolean debug, boolean complete) {
		for(int i = 0 ; i<ps.problems.size(); i++) {
			int looploc = ps.checkLoop(i); 
			if(looploc>=0) {
				boolean check = true;
				for(Binding b: ps.Bindings) 
					if(b.NonTerm.getSym().compareTo(ps.Loops.get(looploc).LoopAUP.Variable.getSym())==0 && 
					   ps.problems.get(i).Variable.getSym().compareTo(b.Right.getSym())==0 &&  
					   ps.Loops.get(looploc).LoopAUP.Variable.getSym().compareTo(ps.problems.get(i).Variable.getSym()) != 0) {
						check = false;
						break;
					}
				if(check) {
					HashSet<Binding> b2 = new HashSet<Binding>();
					for(Binding b: ps.Bindings) b2.add(new Binding(b.NonTerm,b.replace(ps.problems.get(i).Variable,ps.Loops.get(looploc).LoopAUP.Variable)));
					ps.Bindings = b2;
					ps.Bindings.add(new Binding((Const) ps.Loops.get(looploc).LoopAUP.Variable.Dup(),ps.problems.get(i).Variable.Dup()));
					debugFunc("Sat Loop Unit",ps,debug,complete);
				}
			}
		}
		return true;
	}
	private static void probConstruct(AUP prob,ProblemState ps,FunctionDefinition fd ) {
		Const var1 = new Const(ps.nVar(prob.Variable.getSym()));
		Const var2 = new Const(ps.nVar(prob.Variable.getSym()));
		ArrayList<Term> args1 = new ArrayList<Term>();
		ArrayList<Term> args2 = new ArrayList<Term>();
		args1.add(prob.Left.Dup());
		args1.add(new Const("U_"+fd.name));
		args2.add(new Const("U_"+fd.name));
		args2.add(prob.Right.Dup());
		ps.problems.add(ps.problems.size(),new AUP(var1,new Func(fd.name,args1,true),new Func(fd.name,args2,true)));
		ps.Bindings.add(new Binding(((Const)prob.Variable.Dup()),var1));		
		args1 = new ArrayList<Term>();
		args2 = new ArrayList<Term>();
		args1.add(new Const("U_"+fd.name));
		args1.add(prob.Left.Dup());
		args2.add(prob.Right.Dup());
		args2.add(new Const("U_"+fd.name));
		ps.problems.add(ps.problems.size(),new AUP(var2,new Func(fd.name,args1,true),new Func(fd.name,args2,true)));
		ps.Bindings.add(new Binding(((Const)prob.Variable.Dup()),var2));
	}
	
	private static boolean UDHelper1(Term side1,Term side2, ProblemState ps,Const Var,boolean debug) {
		ArrayList<Term> args = new ArrayList<Term>();
		args.add(new Const("U_"+side1.getSym()));
		args.add(side2.Dup());
		Term nleft = new Func(side1.getSym(),args,side1.Unit());
        args = new ArrayList<Term>();
        args.add(side2.Dup());
        args.add(new Const("U_"+side1.getSym()));
		Term nRight = new Func(side1.getSym(),args,side1.Unit());
		String var = ps.nVar(Var.getSym()); 
		ps.problems.add(new AUP(new Const(var),side1.Dup(),nleft));
		ps.Bindings.add(new Binding(((Const)Var.Dup()),((Term)new Const(var))));
		var = ps.nVar(Var.getSym());
		ps.problems.add(new AUP(new Const(var),side1.Dup(),nRight));
		ps.Bindings.add(new Binding(((Const)Var.Dup()),((Term)new Const(var))));		
		return true;
	}
	private static boolean UDHelper2(Term side1,Term side2, ProblemState ps,Const Var,boolean debug) {
		ArrayList<Term> args = new ArrayList<Term>();
		args.add(new Const("U_"+side1.getSym()));
		args.add(side2.Dup());
		Term nleft = new Func(side1.getSym(),args,side1.Unit());
        args = new ArrayList<Term>();
        args.add(side2.Dup());
        args.add(new Const("U_"+side1.getSym()));
		Term nRight = new Func(side1.getSym(),args,side1.Unit());
		String var = ps.nVar(Var.getSym()); 
		ps.problems.add(new AUP(new Const(var),nleft,side1.Dup()));
		ps.Bindings.add(new Binding(((Const)Var.Dup()),((Term)new Const(var))));
		var = ps.nVar(Var.getSym());
		ps.problems.add(new AUP(new Const(var),nRight,side1.Dup()));
		ps.Bindings.add(new Binding(((Const)Var.Dup()),((Term)new Const(var))));		
		return true;
	}
	public static void debugFunc(String message, ProblemState ps, boolean debug, boolean complete) {
		if(debug) {
			System.out.println(message);
			//System.out.println(" \\Lra{\\Rule{"+message+"}} \\\\");
			if(complete) System.out.print(ps.toString());		
		}
		ps.counter++;
	}

}
