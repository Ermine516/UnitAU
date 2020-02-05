
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is a class which implements Term specifically for Logical Constants. This implies that
 * the Term do not take any arguments.
 *
 * @author David M. Cerna
 */
public class Const implements Term {

    private final String Sym;
    private ArrayList<Term> Args;


    public Const(String sym) {
        Sym = sym;
        Args = new ArrayList<>();
    }
    public ArrayList<Term> subTerms() {
        return Args;
    }

    public String getSym() {
        return this.Sym;
    }


    public Term Dup() {
        return new Const(this.Sym);
    }

    
    public Term replace(Const c, Term r) {
        if (c.Sym.matches(this.Sym)) return r.Dup();
        else return this;
    }

    public boolean contains(Const c) {
        return this.equals(c);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        else if ((o instanceof Const)) {
            return this.getSym().compareTo(((Const) o).getSym()) == 0;
        } else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + getSym().hashCode();
        return hash;
    }




    /**
     * Prints the Const object as a string. Used mainly for internal use rather than for Graphical
     * display.
     * @return Const object as a String
     * @author David M. Cerna
     */
    @Override
    public String toString() {
    	if(this.Sym.contains("_")) {
        	String[]splitter = this.Sym.split("_");
        	if(splitter[1].length()>1) {
        		return splitter[0]+"_{"+splitter[1]+"}";
        	}else  return this.Sym;
    	}    	
    	else return this.Sym;
    }
	@Override
	public boolean Unit() {
		return false;
	}
	@Override
	public Term UnitNorm() {
		return this;
	}
	@Override
	public int depth() {
		return 1;
	}

}
