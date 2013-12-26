 public class VariableExpression extends Expression {
	
	////////////////////////
	// Instance Variables //
	////////////////////////

	private final String name_;
	private final String latex_;


	//////////////////
	// Constructors //
	//////////////////

	public VariableExpression(final String name) {
		this.name_ = name;
		this.latex_ = "";
	}
	
	public VariableExpression(final String name, final String latex) {
		this.name_ = name;
		this.latex_ = latex;
	}


	///////////////////
	// Other Methods //
	///////////////////
	
	public String toString() {
		return name_;
	}
	
	public String toLatex() {
		String result = "";
		result = ( (latex_.equals("")) ? name_ : latex_ );
		
		if (this.isSelected()) {
			result = "\\bgcolor{Green}{" + result + "}";
		}
		
		return result;
	}
	
	public boolean equals(final Expression e) {
		return e instanceof VariableExpression && e.toString().equals(name_);
	}
	
	/**
	 * Compare this VariableExpression to another expression.
	 * A VariableExpression goes after an OperatorExpression and before a NumberExpression.
	 * Two VariableExpressions are compared ASCIIbetically by name.
	 * @param e another expression
	 * @return a negative number if this goes before e, or a positive number if this goes after e
	 */
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

