import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("unused")
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
	public final VariableEnvironment variables;
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
		variables = new VariableEnvironment();
		hypotheses = new ArrayList<Statement>();
		conclusions = new ArrayList<Statement>();
		byte currentSection = VARIABLES;
		
		ArrayList<Statement> preliminaryHypotheses = new ArrayList<Statement>();
		ArrayList<Statement> preliminaryConclusions = new ArrayList<Statement>();
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
						/*
						 * Format for variable line is "type name"
						 * Examples:
						 * 	number x
						 * 	number /mu
						 * 	point P
						 */
						String[] words = line.split(" ");
						if (words.length == 1) {
							variables.add(new VariableExpression(words[0], Type.NUMBER));
						}
						else if (words.length == 2) {
							variables.add(new VariableExpression(words[1], Type.fromString(words[0])));
						}
						else {
							// Wrong format! Choke!
							System.out.println("Theorem engine could not parse variable line " + line);
						}
						break;
					case HYPOTHESES:
						preliminaryHypotheses.add(new Statement(line, variables));
						break;
					case CONCLUSIONS:
						preliminaryConclusions.add(new Statement(line, variables));
						break;
					}
			}
		}
		
		/*
		 * Replace untyped variables that occur in hypotheses and conclusions
		 * with the corresponding typed variables from the variables section.
		 */
		for (Statement hypothesis : preliminaryHypotheses) {
			for (VariableExpression var : variables) {
				hypothesis = hypothesis.substitute(var, var);
			}
			hypotheses.add(hypothesis);
		}
		for (Statement conclusion : preliminaryConclusions) {
			for (VariableExpression var : variables) {
				conclusion = conclusion.substitute(var, var);
			}
			conclusions.add(conclusion);
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
	
	@Override
	public String toString() {
		return name;
	}

}
