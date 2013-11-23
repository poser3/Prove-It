import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Theorem {
	
	private static final byte VARIABLES = 0;
	private static final byte HYPOTHESES = 1;
	private static final byte CONCLUSIONS = 2;
	
	static ArrayList<Theorem> theorems = new ArrayList<Theorem>();
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
	static { loadTheorems(); }
	
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
						variables.add(Expression.parse(line));
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
	
	public final String name;
	public final ArrayList<Expression> variables;
	public final ArrayList<Statement> hypotheses;
	public final ArrayList<Statement> conclusions;
	
	public String toString() {
		return name;
	}

}

