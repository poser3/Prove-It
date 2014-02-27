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
							variables.add(new VariableExpression(words[0], Type.fromString(words[1])));
						}
						else {
							// Wrong format! Choke!
							System.out.println("Theorem engine could not parse variable line " + line);
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
	
	@Override
	public String toString() {
		return name;
	}

}
