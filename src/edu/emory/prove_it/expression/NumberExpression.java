package edu.emory.prove_it.expression;
import java.math.BigDecimal;

import edu.emory.prove_it.util.LookAndFeel;

public class NumberExpression extends Expression {
	
	private BigDecimal value;
	
	/**
	 * Make a NumberExpression from a BigDecimal
	 * @param value a BigDecimal representing the value of the expression
	 */
	public NumberExpression(BigDecimal value) {
		this.value = value;
	}
	/**
	 * Make a NumberExpression from an int
	 * @param value an int representing the value of the expression
	 */
	public NumberExpression(int value) {
		this.value = BigDecimal.valueOf(value);
	}
	/**
	 * Make a NumberExpression from a String
	 * @param value a String representing the value of the expression
	 */
	public NumberExpression(String value) {
		this.value = new BigDecimal(value);
	}
	
	public BigDecimal getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	/**
	 * Formats this expression for use in a LaTex context.
	 * @return a LaTex representation of this expression.
	 */
	@Override
	public String toLatex() {
		String result = value.toString();
		if (this.isSelected()) {
			result = "\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}{" + result + "}";
		}
		return result;
	}
	
	/**
	 * Determine whether two expressions are equal.
	 * A NumberExpression is equal to another expression if the other expression is a NumberExpression with the same value as the first.
	 * @param e another expression
	 * @return whether the two expressions are the equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof NumberExpression && ((NumberExpression) obj).getValue().equals(value);
	}
	
	@Override
	public NumberExpression clone() {
		return new NumberExpression(value);
	}
	
	/**
	 * Compare this NumberExpression to another expression
	 * An OperatorExpression or VariableExpression is comes before any NumberExpression.
	 * Two NumberExpressions are compared using BigDecimal's compareTo.
	 * @param e another expression
	 * @return a negative number if this goes before e, or a positive number if this goes after e
	 */
	@Override
	public int compareTo(Expression e) {
		if(e instanceof OperatorExpression || e instanceof VariableExpression)
			return Integer.MAX_VALUE;
		else if(e instanceof NumberExpression)
			return value.compareTo(((NumberExpression) e).getValue());
		else
			throw new ClassCastException();
	}
	
	@Override
	public Type getType() {
		return Type.NUMBER;
	}

}
