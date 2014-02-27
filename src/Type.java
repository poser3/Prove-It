
public enum Type {
	NUMBER("number"), LOGICAL("logical"), POINT("point"), SEGMENT("segment"), RAY("ray"),
	LINE("line"), CIRCLE("circle"), ANGLE("angle");
	
	private final String latex;
	
	Type(String latex) {
		this.latex = latex;
	}
	
	public static Type fromString(String s) {
		switch(s.toLowerCase()) {
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
			return null;
		}
	}
	
	@Override
	public String toString() {
		return latex;
	}
}