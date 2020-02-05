public class AUP {
Term Left;
Term Right;
Const Variable;
public AUP(Const V,Term L,Term R) {
	Variable=V;
	Left = L;
	Right=R;
}
public String toString(){
	return Variable.getSym()+" : "+ Left + " =^= "+ Right;
}
}
