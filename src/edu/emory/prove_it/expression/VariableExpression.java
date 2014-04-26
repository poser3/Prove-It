package edu.emory.prove_it.expression;

import edu.emory.prove_it.util.LookAndFeel;

public class VariableExpression extends Expression {
	 
	///////////////
	// Constants //
	///////////////
	
	private final String name_;
	private final Type type_;

	public VariableExpression(final String name) {
		this.name_ = name;
		this.type_ = Type.NUMBER;
	}
	
	public VariableExpression(final String name, Type type) {
		this.name_ = name;
		this.type_ = type;
	}
	
	@Override
	public String toString() {
		return name_;
	}
	
	@Override
	public String toLatex() {
		if (this.isSelected())
			return "\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}{" + name_ + "}";
		else
			return name_;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof VariableExpression && ((VariableExpression) obj).toString().equals(name_);
	}
	
	@Override
	public VariableExpression clone() {
		return new VariableExpression(name_, type_);
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
	
	@Override
	public Type getType() {
		return type_;
	}

}
