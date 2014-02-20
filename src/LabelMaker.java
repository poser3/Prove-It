/**
 * The LabelMaker produces sequential labels for points, segments, lines, circles, and rays.
 * Points are labeled, in the order they are created, A, B, C, ..., Z, AA, AB, ..., AZ, BA, ...
 * (using the equivalent of a base-26 number system). Other objects use the same scheme, but
 * with lowercase letters. Labels for each type of object are tracked separately.
 * @author Paul Oser, Lee Vian
 *
 */
public class LabelMaker {
	
	public static final byte POINT = 0;
	public static final byte LINE = 2;
	public static final byte CIRCLE = 3;
	public static final byte RAY = 4;
	
	private String currentPointLabel_;
	private int currentLineNum_;
	private int currentCircleNum_;
	private int currentRayNum_;
	
	public LabelMaker() {
		currentPointLabel_ = "A";
		currentLineNum_ = 1;
		currentCircleNum_ = 1;
		currentRayNum_ = 1;
	}
	
	/**
	 * Find the label that comes after the provided label, as a base-26 number (in a String)
	 * @param label
	 * @return the next label
	 */
	private String next(String label) {
		if (label.length() == 1) {
			if (label == "z")
				return "aa";
			else if (label == "Z")
				return "AA";
			else {
				char l = label.toCharArray()[0];
				return Character.toString((char) (l + 1));
			}
		}
		else {
			String beginning = label.substring(0, label.length()-1);
			char end = label.substring(label.length()-1).toCharArray()[0];
			if (end == 'z')
				return next(beginning) + "a";
			else if (end == 'Z')
				return next(beginning) + "A";
			else {
				return beginning + Character.toString((char) (end + 1));
			}
		}
	}
	
	/**
	 * Return the next label of the specified type. This method is not purely functional;
	 * calling it several times in succession with the same argument returns that many
	 * consecutive labels.
	 * @param labelType one of LabelMaker's byte constants, corresponding to a type of geometric object
	 * @return the next label for that type of object
	 */
	public String nextLabel(byte labelType) {
		String label;
		switch (labelType) {
		case POINT :	label = currentPointLabel_;
						currentPointLabel_ = next(currentPointLabel_);
						break;
		case LINE :		label = "L" + currentLineNum_;
						currentLineNum_++;
						break;
		case CIRCLE :	label = "C" + currentCircleNum_;
						currentCircleNum_++;
						break;
		case RAY :		label = "R" + currentRayNum_;
						currentRayNum_++;
						break;
		default :		throw new IllegalArgumentException();
		}
		return label;
	}

}
