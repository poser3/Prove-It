import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class SettingsReader {
	
	private static boolean fileRead = false;
	private static HashMap<String, String> settings;
	
	private static final HashMap<String, String> defaults = new HashMap<String, String>();
	static {
		defaults.put("theorem-path", "../res/theorems/");
	}
	
	public static String getSetting(String key) {
		if (! fileRead)
			readFile();
		
		return settings.get(key);
	}
	public static void resetFileReadStatus() {
		fileRead = false;
	}
	
	private static void readFile() {
		settings = new HashMap<String, String>();
		
		try {
			Scanner scanner = new Scanner(new File("../settings.txt"));
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] tokens = line.split("=");
				for (int i=0; i<tokens.length; i++)
					tokens[i] = tokens[i].trim();
				
				if (tokens.length == 1)
					settings.put(tokens[0], "true");
				else if (tokens.length == 2)
					settings.put(tokens[0], tokens[1]);
			}
			
			fileRead = true;			
			scanner.close();
		}
		catch (FileNotFoundException e) {
			// Complain
			System.out.println("Settings file not found!");
			fileRead = false;
			
			// Default values for settings
			settings = defaults;
		}
	}

}
