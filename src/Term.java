import java.util.ArrayList;
//General term Interface 
public interface Term  {


    ArrayList<Term> subTerms();

    String getSym();

    Term replace(Const c, Term r);

    boolean contains(Const c);

    Term Dup();

    String toString();

    boolean equals(Object o);

    int hashCode();

	boolean Unit();

	Term UnitNorm();
	int depth();

}

