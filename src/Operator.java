import java.util.ArrayList;
import java.util.Collections;

public class Operator implements Comparable<Operator> {
	
	Operator(String name) {
		this.name = name;
	}
	
	private final String name;
	public final short precedence = 0;
	public final boolean isAssociative = false;
	public final boolean isCommutative = false;
	private final String distributes = null;
	private final String inverse = null;
	
	/**
	 * Checks if this operator distributes over another.
	 * @param op
	 * @return true if this operator distributes over op, or false if it does not
	 */
	public boolean distributesOver(Operator op) {
		return op.toString().equals(distributes);
	}
	/**
	 * Returns this operator's inverse, if it has one.
	 * @return this operator's inverse, or null if it doesn't have one
	 */
	public Operator inverse() {
		return inverse == null ? null : Operators.named(inverse);
	}
	/**
	 * Check whether two OperatorExpressions using this operator are equal.
	 * The default implementation compares the number of arguments and then each argument in order.
	 * @param e1 an OperatorExpression using this operator
	 * @param e2 another OperatorExpression using this operator
	 * @return true if e1 and e2 represent the same thing and false otherwise
	 */
	public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
		if (! e1.getOp().equals(e2.getOp()))
			return false;
		
		if (e1.getNumArgs() != e2.getNumArgs())
			return false;
		
		for (int i=0; i<e1.getNumArgs(); i++)
			if (! e1.getArg(i).equals(e2.getArg(i)))
				return false;
		
		return true;
	}
	
	@Override
	public final String toString() {
		return name;
	}
	/**
	 * Converts an OperatorExpression using this operator to LaTeX form.
	 * @param e an OperatorExpression using this operator
	 * @return the LaTeX representation of e
	 */
	public String toLatex(final OperatorExpression e) {
		if (e.getNumArgs() == 0)
			return e.getOp().name;
		else if (e.getNumArgs() == 1) {
			StringBuilder sb = new StringBuilder();
			sb.append(e.getOp());
			if (e.getArg(0) instanceof OperatorExpression) {
				sb.append(" (");
				sb.append(e.getArg(0).toLatex());
				sb.append(")");
			}
			else {
				sb.append(" ");
				sb.append(e.getArg(0).toLatex());
			}
			return sb.toString();
		}
		else {
			StringBuilder sb = new StringBuilder();
			if (e.getArg(0) instanceof OperatorExpression) {
				sb.append("(");
				sb.append(e.getArg(0).toLatex());
				sb.append(")");
			}
			else
				sb.append(e.getArg(0).toLatex());
			
			for (int i=1; i<e.getNumArgs(); i++) {
				sb.append(" ");
				sb.append(e.getOp());
				if (e.getArg(i) instanceof OperatorExpression) {
					sb.append(" (");
					sb.append(e.getArg(i).toLatex());
					sb.append(")");
				}
				else {
					sb.append(" ");
					sb.append(e.getArg(i).toLatex());
				}
			}
			return sb.toString();
		}
	}
	
	/**
	 * Determines whether two operators are the same, based on their names.
	 * @param op
	 * @return true if this has the same name as op, or false otherwise
	 */
	public final boolean equals(Operator op) {
		return name.equals(op.toString());
	}
	/**
	 * Compares this operator to another. Operators are sorted ASCIIbetically by name.
	 */
	@Override
	public int compareTo(Operator op) {
		return name.compareTo(op.toString());
	}

	/**
	 * Performs simplifications on an OperatorExpression using this operator.
	 * The default implementation just tries to simplify each argument.
	 * @param e an OperatorExpression using this operator
	 * @return a simpler Expression with the same mathematical meaning
	 */
	public Expression simplify(final OperatorExpression e) {
		ArrayList<Expression> args = new ArrayList<Expression>();
		for (Expression arg : e.getArgs()) {
			if (arg instanceof OperatorExpression)
				args.add(((OperatorExpression) arg).simplify());
			else
				args.add(e);
		}
		return new OperatorExpression(e.getOp(), args);
	}

	/**
	 * This OperatorSpec overwrites the areEqual method to support commutative operators.
	 * @author Lee Vian
	 *
	 */
	public static class CommutativeOperator extends Operator {

		CommutativeOperator(String name) {
			super(name);
		}
		
		public final boolean isCommutative = true;
		
		/**
		 * Check whether two OperatorExpressions using this operator are equal.
		 * This implementation sorts the arguments before comparing.
		 * @param e1
		 * @param e2
		 * @return true if e1 and e2 represent the same thing and false otherwise.
		 */
		@Override
		public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
			if (e1.getNumArgs() != e2.getNumArgs())
				return false;
			
			// Clone the argument lists because sorting happens in-place
			ArrayList<Expression> args1 = new ArrayList<Expression>(e1.getArgs());
			ArrayList<Expression> args2 = new ArrayList<Expression>(e2.getArgs());
			Collections.sort(args1);
			Collections.sort(args2);
			
			for (int i=0; i<args1.size(); i++)
				if (! args1.get(i).equals(args2.get(i)))
					return false;
			
			return true;
		}
		
	}
	
}
