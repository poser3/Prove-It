
public enum Type {
	NUMBER, LOGICAL, POINT, SEGMENT, RAY, LINE, CIRCLE, ANGLE;
	
	public Type fromString(String s) {
		switch(s) {
		case "number":
			return NUMBER;
		case "logical":
			return LOGICAL;
		case "point":
			return POINT;
		case "segment":
			return SEGMENT;
		case "ray":
			return RAY;
		case "line":
			return LINE;
		case "circle":
			return CIRCLE;
		case "angle":
			return ANGLE;
		default:
			throw new IllegalArgumentException(s + " is not a type!");
		}
	}
}