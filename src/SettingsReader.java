import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The SettingsReader provides an interface to a text file which stores settings for the
 * program.
 * 
 * Settings are stored as key-value pairs of Strings. The settings.txt file should live in the
 * top-level directory of the project (outside of the bin, res, and src folders). Each line in
 * the settings file can take one of two forms. If the line looks like "key=value", then
 * everything on each side of the = sign is trimmed, and a settings is created with that key
 * and that value. If the line doesn't have an equals sign, its contents are made the key of a
 * setting, and the value of that setting is the String "true".
 * 
 * @author Lee Vian
 */
public class SettingsReader {
	
	private static boolean fileRead = false;
	private static HashMap<String, String> settings;
	
	private static final HashMap<String, String> defaults = new HashMap<String, String>();
	static {
		// Each new setting should have a default value here.
		defaults.put("image-path", "../res/images/");
		defaults.put("theorem-path", "../res/theorems/");
	}
	
	/**
	 * Look up a setting's value from its key.
	 * 
	 * @param key the key for the setting to look up
	 * @return the value of the setting with that key
	 */
	public static String getSetting(String key) {
		if (! fileRead)
			readFile();
		
		String fromFile = settings.get(key);
		return fromFile != null ? fromFile : defaults.get(key);
	}
	/**
	 * Ask the SettingsReader to reload the settings file the next time someone asks for a
	 * setting.
	 */
	public static void resetFileReadStatus() {
		fileRead = false;
	}
	
	private static void readFile() {
		// Throw away whatever settings we were using before
		settings = new HashMap<String, String>();
		
		try {
			// Read in new settings
			Scanner scanner = new Scanner(new File("../settings.txt"));
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] tokens = line.split("=");
				for (int i=0; i<tokens.length; i++)
					tokens[i] = tokens[i].trim();
				
				if (tokens.length == 1) // lines without = signs are keys whose values are "true"
					settings.put(tokens[0], "true");
				else if (tokens.length == 2) // lines with equal signs are key=value pairs
					settings.put(tokens[0], tokens[1]);
			}
			
			fileRead = true; // the file was successfully read; don't read it again next time
			scanner.close();
		}
		catch (FileNotFoundException e) {
			// Complain
			System.out.println("Settings file not found!");
			fileRead = false; // the file was not successfully read; try again next time
			
			// Default values for settings
			settings = defaults;
		}
	}

}