import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Theorem {
	
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
		hypotheses = new ArrayList<Expression>();
		conclusions = new ArrayList<Expression>();
		ArrayList<Expression> currentSection = variables;
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() > 0) {
				if (line.contains("variables"))
					currentSection = variables;
				else if (line.contains("hypotheses"))
					currentSection = hypotheses;
				else if (line.contains("conclusions"))
					currentSection = conclusions;
				else
					currentSection.add(Expression.parse(line));
			}
		}
		
		scanner.close();
	}
	
	public final String name;
	public final ArrayList<Expression> variables;
	public final ArrayList<Expression> hypotheses;
	public final ArrayList<Expression> conclusions;
	
	public String toString() {
		return name;
	}

}

