import java.util.ArrayList;

/**
 * This is a class which implements functions with a particular arity for the construction Logical
 * Terms.
 * @author David M. Cerna
 */
public final class Func implements Term {

    private final String Sym;
    private final ArrayList<Term> Args;
    private final boolean Unit;

    Func(String sym, ArrayList<Term> args, boolean unit) {
        this.Sym = sym;
        this.Args = args;
        this.Unit = unit;
    }

    public ArrayList<Term> subTerms() {
        return this.Args;
    }

    public boolean Unit() {
        return Unit;
    }

  
    public String getSym() {
        return this.Sym;
    }

    public Term Dup() {
        ArrayList<Term> newArgs = new ArrayList<>();
        for (int i = 0; i < this.Args.size(); i++) newArgs.add(this.Args.get(i).Dup());
        return new Func(this.Sym, newArgs, this.Unit);
    }

    public Term replace(Const c, Term r) {
        ArrayList<Term> newArgs = new ArrayList<>();
        for (int i = 0; i < this.Args.size(); i++) newArgs.add(this.Args.get(i).replace(c, r));
        return new Func(this.Sym, newArgs, this.Unit);
    }

    
    public boolean contains(Const c) {
        boolean ret = false;
        for (int i = 0; i < this.Args.size(); i++)
            if (!ret) ret = this.Args.get(i).contains(c);
        return ret;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + getSym().hashCode();
        for (Term t : subTerms())
            hash = 31 * hash + t.hashCode();
        return hash;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        else if ((o instanceof Func)) {
            if (this.getSym().compareTo(((Func) o).getSym()) == 0) {
                boolean areequal = true;
                for (int i = 0; i < this.subTerms().size(); i++)
                    areequal &= this.subTerms().get(i).equals(((Term) o).subTerms().get(i));
                return areequal;
            } else return false;
        } else if ((o instanceof Const)) {
            if (this.subTerms().size() == 0)
                return this.getSym().compareTo(((Const) o).getSym()) == 0;
            else return false;
        } else return false;
    }
    public String toString() {
        StringBuilder s = new StringBuilder(Sym + "(");
        int i = 0;
        for (; i < (this.Args.size() - 1); i++) s.append(this.Args.get(i).toString()).append(",");
        Term t = this.Args.get(i);
        String ss = t.toString();
        if (this.Args.size() > 0) s.append(ss).append(")");
        return s.toString();
    }

	@Override
	public Term UnitNorm() {
		if(this.Unit) {
			for(int i=0;i<this.Args.size(); i++) 
				if(this.Args.get(i).getSym().compareTo("U_"+this.getSym()) == 0)
					return Args.get((i+1)%2).UnitNorm();
		}
	    return this;
	}

	@Override
	public int depth() {
		if(this.Args.size() == 0) return 1;
		else{
			int ret = 0;
			for(int i=0; i< this.Args.size();i++) {
				int temp = Args.get(i).depth();
				if(Args.get(i).depth()>=ret) ret = temp;	
			}
			return ret+1;
		}
	}





	

}
