import java.util.ArrayList;


class TermHelper {
	class FormatException extends Exception {
		private static final long serialVersionUID = 1L;}

    static Term parse(String s, ProblemState PS) throws FormatException {
        Term result = pI(clean(s), PS).get(0).get(0);
        if (clean(s).compareTo(result.toString()) != 0)
            throw (new TermHelper()).new FormatException();
		else return result;
	}

    private static ArrayList<ArrayList<Term>> pI(String s, ProblemState PS) {
//Finds first left peren
        String[] pL = s.split("(\\s*[(]\\s*)+?", 2),
//Finds first comma peren
                pC = s.split("(\\s*[,]\\s*)+?", 2),
//Finds first right peren
                pR = s.split("(\\s*[)]\\s*)+?", 2);
//if a comma or a left peren is found then continue, otherwise we have reached the end of the term		
        ArrayList<ArrayList<Term>> res = (pL[0].contains(",")) ? pI(pC[1], PS) : (pL.length == 2) ? pI(pL[1], PS) : new ArrayList<ArrayList<Term>>();
//When a comma is found in 	pL[0] we check if there is also right peren indicating a nested term
//Right Perens are replaced by empty arrays indicating unknown function nesting
        if(pL[0].contains(",")){
            if(pC[0].contains(")"))
                for(int i = 0; i<(pC[0].length() - pC[0].replace(")", "").length()); i++)
                    res.add(0,new ArrayList<Term>());
        }
//pL of length 2 indicates that a function symbol occurred and we need to introduce a node using the previous 
//computed terms
        else if(pL.length == 2){
            boolean infixity = false;
            for (FunctionDefinition p : PS.Signature)
                if (pL[0].compareTo(p.name) == 0) infixity = p.unity;
            Func f = new Func(pL[0], res.remove(0), infixity);
            if(res.size()==0) res.add(0,new ArrayList<Term>());
            res.get(0).add(0,f);
        }
//Otherwise we have reached a constant
        else res.add(new ArrayList<Term>());
//There may be more than one constant separated by a comma
        if(pL.length != 2 || pL[0].contains(","))
            res.get(0).add(0,new Const((pC[0].contains(")")|| (!pL[0].contains(",") && pL.length != 2))? pR[0]:pC[0]));
        return res;
    }

    private static String clean(String s) throws FormatException {
		String[] spaces = s.split("\\s*");
        StringBuilder ret = new StringBuilder();
		if(spaces.length==0) throw (new TermHelper()).new FormatException();
		for(String ss:spaces)
            if (!ss.contains("\\s*")) ret.append(ss);
        return ret.toString();
    }

    static boolean containsNestedSequents(Term t) {
        if (t instanceof Const)
            return false;
        else if (t instanceof Func && t.getSym().compareTo("⊢") == 0) {
            return !containsNestedSequents(t.subTerms().get(0)) && !containsNestedSequents(t.subTerms().get(1));
        } else if (t instanceof Func) {
            boolean ret = false;
            for (int i = 0; i < t.subTerms().size(); i++)
                ret |= containsNestedSequents(t.subTerms().get(i));
            return ret;
        } else return false;
    }

}
