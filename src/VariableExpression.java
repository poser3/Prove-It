 public class VariableExpression extends Expression {
	 
	///////////////
	// Constants //
	///////////////
	
	//--none--
	
	
	////////////////////////
	// Instance Variables //
	////////////////////////

	private final String name_;
	private final String latex_;


	//////////////////
	// Constructors //
	//////////////////

	public VariableExpression(final String type, final String name) {
		this.setType(type);
		this.name_ = name;
		this.latex_ = "";
	}
	
	public VariableExpression(final String type, final String name, final String latex) {
		this.setType(type);
		this.name_ = name;
		this.latex_ = latex;
	}


	///////////////////
	// Other Methods //
	///////////////////
	
	public static String getVarTypeFromString(String s) {
		int posOfUnderscore = s.indexOf("_");
		String varType = "unknown";
		if (posOfUnderscore != -1) {
			varType = s.substring(0, posOfUnderscore);
			s = s.substring(posOfUnderscore + 1);
		}
		
		return varType;
	}
	
	public static String getVarNameFromString(String s) {
		String varName = s;
		int posOfUnderscore = s.indexOf("_");
		if (posOfUnderscore != -1) {
			varName = s.substring(posOfUnderscore + 1);
		}
		
		return varName;
	}
	
	public String getTypeInLatex() {
		switch (this.getType()) {
			case "unknown" : return "\\textrm{unknown }";
			case "realValue" : return "\\textrm{real value }";
			case "point" : return "\\textrm{point }";
			case "line" : return "\\textrm{line }";
			case "ray" : return "\\textrm{ray }";
			case "segment" : return "\\textrm{segment }";
			case "circle" : return "\\textrm{circle }";
			case "statement" : return "\\textrm{statement }";
			default : return "\\textrm{unknown }";
		}
	}
	
	@Override
	public String toString() {
		return name_;
	}
	
	@Override
	public String toLatex() {
		String result = "";
		result = latex_.equals("") ? name_ : latex_;
		
		if (this.isSelected()) {
			result = "\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}{" + result + "}";
		}
		
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof VariableExpression && ((VariableExpression) obj).toString().equals(name_);
	}
	
	/**
	 * Compare this VariableExpression to another expression.
	 * A VariableExpression goes after an OperatorExpression and before a NumberExpression.
	 * Two VariableExpressions are compared ASCIIbetically by name.
	 * @param e another expression
	 * @return a negative number if this goes before e, or a positive number if this goes after e
	 */
	@Override
	public int compareTo(final Expression e) {
		if(e instanceof OperatorExpression)
			return Integer.MAX_VALUE;
		else if(e instanceof VariableExpression)
			return name_.compareTo(e.toString());
		else if(e instanceof NumberExpression)
			return Integer.MIN_VALUE;
		else
			throw new ClassCastException();
	}

}
