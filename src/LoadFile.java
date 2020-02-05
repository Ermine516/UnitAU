import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

 public class LoadFile {
	static ProblemState loadFile(FileInputStream IS, String file) {
        ProblemState newPS = new ProblemState();
        String line;
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(IS))) {
            while ((line = br.readLine()) != null) {
                if (line.matches("\\s+")) continue;
                lineCount++;
                String[] parts = line.split("\\s+");
                if (parts[0].compareTo("Function:") == 0)
                    parseFunctionSymbol(newPS, parts);
                else if (parts[0].compareTo("Problem:") == 0) {
                    System.out.print(file + ":");
                    parseProblemDefinition(newPS, parts);
                }
                else throw new IOException();
            }
        } catch (Exception ex) {
            System.out.println("Syntax error on line " + lineCount + " of " + file + ".");
            newPS = new ProblemState();

        }
        return newPS;
    }


    private static boolean parseProblemDefinition(ProblemState PS, String[] parts) throws TermHelper.FormatException {
        if (parts.length != 4) throw new TermHelper().new FormatException();
        ArrayList<AUP> problems = new ArrayList<AUP>();
        PS.problems.add(new AUP(new Const(parts[1]),TermHelper.parse(parts[2], PS),TermHelper.parse(parts[3], PS)));
        return true;
    }
    private static void parseFunctionSymbol(ProblemState PS, String[] parts) throws TermHelper.FormatException {
        if (parts.length != 4) throw new TermHelper().new FormatException();
            if(parts[3].matches("Unit")){
            	PS.UnitFunc.add(new FunctionDefinition(parts[1].toString(), Integer.parseInt(parts[2]), true));
                PS.Signature.add(new FunctionDefinition(parts[1].toString(), Integer.parseInt(parts[2]), true));
                PS.Signature.add(new FunctionDefinition("U_"+parts[1].toString(), 0, false));
            }
            else {
            	PS.Signature.add(new FunctionDefinition(parts[1].toString(), Integer.parseInt(parts[2]), false));
            }

    }
}
