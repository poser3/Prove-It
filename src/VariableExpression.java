public class VariableExpression extends Expression {
	
	private final String name;

	public VariableExpression(final String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	public String toLatex() {
		return name;
	}
	
	public boolean equals(final Expression e) {
		return e instanceof VariableExpression && e.toString().equals(name);
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
			return name.compareTo(e.toString());
		else if(e instanceof NumberExpression)
			return Integer.MIN_VALUE;
		else
			throw new ClassCastException();
	}

}

