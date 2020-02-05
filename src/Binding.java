public class Binding {
public Const NonTerm;
public Term  Right;

public Binding(Const c, Term t) {
	NonTerm = c;
	Right = t;
}
public String toString(){
	return NonTerm.getSym()+" |--> "+ Right;
}

public Term replace(Const c, Term r) {
	Right = Right.replace( c,  r);
	return Right;
}

@Override
public int hashCode() {
    return this.toString().hashCode();
}

@Override
public boolean equals(Object o) {
    if(!(o instanceof Binding)) return false;
    return this.toString().compareTo(((Binding)o).toString())==0;
 
}

}
