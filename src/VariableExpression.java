public class VariableExpression extends Expression {
	
	private final String name;
	private final Type type;

	public VariableExpression(final String name) {
		this.name = name;
		this.type = Type.NUMBER;
	}
	
	public VariableExpression(final String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return name;
	}
	@Override
	public String toLatex() {
		return name;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof VariableExpression && ((VariableExpression) obj).toString().equals(name);
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
			return name.compareTo(e.toString());
		else if(e instanceof NumberExpression)
			return Integer.MIN_VALUE;
		else
			throw new ClassCastException();
	}
	
	@Override
	public Type getType() {
		return type;
	}

}

