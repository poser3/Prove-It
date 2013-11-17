import java.util.ArrayList;

public class OperatorExpression extends Expression {
	
	private Operator op;
	private ArrayList<Expression> arguments;
	
	/**
	 * Construct an OperatorExpression with the given operator and arguments.
	 * @param op the operator for the expression
	 * @param arguments an ArrayList of expressions to which the operator will be applied
	 */
	public OperatorExpression(Operator op, ArrayList<Expression> arguments) {
		this.op = op;
		this.arguments = arguments;
	}	
	public OperatorExpression(final String op, ArrayList<Expression> arguments) {
		this.op = Operator.named(op);
		this.arguments = arguments;
	}

	public Operator getOp() {
		return op;
	}
	public ArrayList<Expression> getArgs() {
		return arguments;
	}
	public int getNumArgs() {
		return arguments == null ? 0 : arguments.size();
	}
	/**
	 * Gets the ith argument to this expression
	 * @param i the index of the argument. The first argument has index i=0.
	 * @return the argument at position i in the list
	 */
	public Expression getArg(int i) {
		if(arguments == null || i < 0 || i >= arguments.size())
			throw new ArrayIndexOutOfBoundsException();
		
		return arguments.get(i);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// The operator, followed by each argument enclosed in parentheses
		sb.append(op.toString());
		for(int i=0; i<arguments.size(); i++) {
			sb.append(' ');
			if(arguments.get(i) instanceof OperatorExpression) {
				sb.append('(');
				sb.append(arguments.get(i).toString());
				sb.append(')');
			}
			else
				sb.append(arguments.get(i).toString());
		}
		
		return sb.toString();
	}
	public String toLatex() {
		return op.toLatex(this);
	}
	
	public boolean equals(final Expression e) {
		// OperatorExpressions are equal if the operator is equal and each argument is equal
		if (e instanceof OperatorExpression) {
			if (! ((OperatorExpression) e).getOp().equals(op))
				return false;
			
			return op.areEqual(this, (OperatorExpression) e);
		}
		// An OperatorExpression is not equal to a NumberExpression or a VariableExpression
		else return false;
	}
	
	/**
	 * Compare this OperatorExpression to another expression.
	 * An OperatorExpression goes before a VariableExpression or NumberExpression.
	 * Two OperatorExpressions are compared by their operators, and then by each argument in order.
	 * @param e another expression
	 * @return a negative number if this goes before e, or a positive number if this goes after e
	 */
	public int compareTo(final Expression e) {
		if(e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			// equal expressions are interchangeable
			if (equals(oe))
				return 0;
			
			// first try to compare by operator
			if (! op.equals(oe.getOp()))
				return op.compareTo(oe.getOp());
			
			// more arguments come first
			if (getNumArgs() > oe.getNumArgs())
				return -1;
			else if (getNumArgs() < oe.getNumArgs())
				return 1;
			else
				// sort by the first different argument
				for (int i=0; i<getNumArgs(); i++) {
					if (! getArg(i).equals(oe.getArg(i)))
						return getArg(i).compareTo(oe.getArg(i));
				}
			
			// at this point all the arguments were the same, so give up
			// (this should be covered by the equality check up top)
			return 0;
			
		}
		// OperatorExpressions come before other types of expression
		else if(e instanceof VariableExpression || e instanceof NumberExpression)
			return Integer.MIN_VALUE;
		else
			throw new ClassCastException();
	}
	
	public Expression simplify() {
		return op.simplify(this);
	}

}
