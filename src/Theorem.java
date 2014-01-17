import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Theorem {
	
	///////////////
	// Constants //
	///////////////
	
	private static final byte VARIABLES = 0;
	private static final byte HYPOTHESES = 1;
	private static final byte CONCLUSIONS = 2;
	
	
    ////////////////////////
	// Instance Variables //
	////////////////////////
	
	public final String name;
	public final ArrayList<Expression> variables;
	public final ArrayList<Statement> hypotheses;
	public final ArrayList<Statement> conclusions;
	
	static ArrayList<Theorem> theorems = new ArrayList<Theorem>();
	
	
	//////////////////////////
	// Class Initialization //
	//////////////////////////
	
	static { loadTheorems(); }
	
	
	/////////////////
	// Constructor //
	/////////////////
	
	private Theorem(File file) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(file);
				
		name = scanner.nextLine();
		variables = new ArrayList<Expression>();
		hypotheses = new ArrayList<Statement>();
		conclusions = new ArrayList<Statement>();
		byte currentSection = VARIABLES;
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() > 0) {
				if (line.contains("variables"))
					currentSection = VARIABLES;
				else if (line.contains("hypotheses"))
					currentSection = HYPOTHESES;
				else if (line.contains("conclusions"))
					currentSection = CONCLUSIONS;
				else
					switch(currentSection) {
					case VARIABLES:
						//Format for variable line should be: "type_name latex"
						//Examples:
						//  realNumber_x x
						//  realNumber_mu /mu
						//  point_P P
						int posOfFirstSpace = line.indexOf(" ");
						String varType;
						String varName;
						String varLatex;
						if (posOfFirstSpace != -1) {
							String varTypeAndName = line.substring(0, posOfFirstSpace);
							varType = VariableExpression.getVarTypeFromString(varTypeAndName);
							varName = VariableExpression.getVarNameFromString(varTypeAndName);
							varLatex = line.substring(posOfFirstSpace+1);
							variables.add(new VariableExpression(varType, varName, varLatex));
						}
						else {
							varType = VariableExpression.getVarTypeFromString(line);
							varName = VariableExpression.getVarNameFromString(line);
							variables.add(new VariableExpression(varType, varName));
						}
						break;
					case HYPOTHESES:
						hypotheses.add(new Statement(line));
						break;
					case CONCLUSIONS:
						conclusions.add(new Statement(line));
						break;
					}
			}
		}
		
		scanner.close();
	}
	
	///////////////////
	// Other Methods //
	///////////////////
	
	public static void loadTheorems() {
		theorems = new ArrayList<Theorem>();
		File folder = new File(SettingsReader.getSetting("theorem-path"));
		for (File file : folder.listFiles()) {
			try {
				theorems.add(new Theorem(file));
			}
			catch (FileNotFoundException e) {}
		}
	}
	
	public String toString() {
		return name;
	}

}
