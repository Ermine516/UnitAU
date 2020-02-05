
public class Loop {
AUP LoopAUP;
int unit;

public Loop(AUP a, int i) {
	LoopAUP = a; 
	unit = i;
}
public String toString(){
	return "( "+LoopAUP.toString()+" , "+ unit+" )";
}
}
