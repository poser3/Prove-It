import java.util.ArrayList;

public class OperatorExpression extends Expression {

	////////////////////////
	// Instance Variables //
	////////////////////////
	
	private Operator op;
	private ArrayList<Expression> arguments;

	//////////////////
	// Constructors //
	//////////////////
	
	/**
	 * Construct an OperatorExpression with the given operator and arguments.
	 * @param op the operator for the expression
	 * @param arguments an ArrayList of expressions to which the operator will be applied
	 */
	public OperatorExpression(Operator op, ArrayList<Expression> arguments) {
		this.op = op;
		this.arguments = arguments;
	}	
	
	
	/**
	 * Construct an OperatorExpression with the given operator (as named by some string) and arguments.
	 * @param the name of the operator for the expression
	 * @param arguments an ArrayList of expressions to which the operator will be applied
	 */
	public OperatorExpression(final String op, ArrayList<Expression> arguments) {
		this.op = Operators.named(op);
		this.arguments = arguments;
	}
	
	
	///////////////////////
	// Getters & Setters //
	///////////////////////

	/**
	 * Returns the operator for this OperatorExpression
	 * @return the operator for this OperatorExpression
	 */
	public Operator getOp() {
		return op;
	}
	
	
	/**
	 * Returns the arguments for this OperatorExpression
	 * @return the arguments for this OperatorExpression
	 */
	public ArrayList<Expression> getArgs() {
		return arguments;
	}
	
	
	// TODO: I added the methods replace() and replaceAll() below to help with the substituteM() 
	//method in Expression class. Note, they increase the mutability of OperatorExpression, 
	//which we may not wish to have... P.O.

	/**
	 * Returns this operatorExpression (as an expression) with the argument at position pos replaced by replacement
	 * @param pos - the position of the expression in arguments to be replaced
	 * @param replacement - the replacement expression
	 * @return the expression replaced, throws indexOutOfBounds exception if pos was not a legal position
	 */
	public Expression replace(int pos, Expression replacement) {
		arguments.add(pos, replacement);
		return arguments.remove(pos + 1);
	}
	
	/**
	 * Returns the number of arguments for this OperatorExpression
	 * @return the number of arguments for this OperatorExpression (= 0 if arguments is null)
	 */
	public int getNumArgs() {
		return arguments == null ? 0 : arguments.size();  
	}
	
	
	/**
	 * Returns the ith argument of this expression. Throws ArrayIndexOutOfBoundsException 
	 * if getArgs() is null, or if i does not represent a legal index.
	 * @param i the index of the argument. The first argument has index i=0.
	 * @return the argument at position i in the list
	 */
	public Expression getArg(int i) {
		if(arguments == null || i < 0 || i >= arguments.size())
			throw new ArrayIndexOutOfBoundsException();
		
		return arguments.get(i);
	}
	
	
	///////////////////
	// Other Methods //
	///////////////////
	
	/**
	 * Returns a console-friendly string version of this operatorExpression, mostly for debugging purposes.
	 * The string is similar to the template string in Expression.parse().
	 * Example: "+ (- x y) a b (* c d)"
	 * This is a prefix form for the operatorExpression, so the operator comes first followed by each of its arguments.
	 * @return the string version of this operatorExpression
	 */
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		//start with the operator first
		sb.append(op.toString());
		
		//now list the arguments, wrapping any arguments that are themselves operatorExpressions in parentheses
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
	
	
	/**
	 * Returns a LaTeX version of this operatorExpression by appealing to the 
	 * toLatex() method of its operator, adding highlighting if this operatorExpression
	 * is selected 
	 * @return a LaTeX version of this expression
	 */
	public String toLatex() {
		if (this.isSelected()) {
			return "\\bgcolor{Green}{" + op.toLatex(this) + "}";
		}
		else {
			return op.toLatex(this);
		}
	}
	
	/**
	 * Returns true if the two operatorExpressions are identical - that is to say, 
	 * they have the same operator and the same arguments.  The areEqual() method 
	 * of the Operator class does most of the heavy lifting here.
	 */
	public boolean equals(final Expression e) {
		// operatorExpressions are equal if the operators are equal and 
		// the corresponding arguments are equal (possibly in either order,
		// depending on the operator)
		if (e instanceof OperatorExpression) {
			if (! ((OperatorExpression) e).getOp().equals(op))
				return false;
			
			// as what to do with the arguments depends on which operator
			// we have, we appeal to the areEqual() method of the Operator class...
			return op.areEqual(this, (OperatorExpression) e);
		}
		// No operatorExpression is equal to a numberExpression or a variableExpression
		else return false;
	}
	
	
	/**
	 * Compare this OperatorExpression to another expression, according to the following rules:
	 *   1. An operatorExpression always comes before a variableExpression or numberExpression.
	 *   2. Two operatorExpressions are compared by their operator first 
	 *      (returning this.getOp().compareTo(e.getOp()) if it is not zero)
	 *   3. Between two operatorExpressions with the same operator, the one with more arguments comes first
	 *   4. Between two operatorExpressions with the same operator and number of arguments, 
	 *      the arguments themselves are compared, in order returning getArg(i).compareTo(e.getArg(i))
	 *      for the first one that is different, and 0 if there are no arguments that are different (i.e.
	 *      the operatorExpressions are equal)
	 * @param e another expression
	 * @return a negative number if this goes before e, or a positive number if this goes after e
	 */
	public int compareTo(final Expression e) {
		
		if(e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			// equal expressions are interchangeable
			if (equals(oe)) {
				return 0;
			}
			
			// first try to compare by operator
			if (! op.equals(oe.getOp())) {
				return op.compareTo(oe.getOp());
			}
			
			// more arguments come first
			if (getNumArgs() > oe.getNumArgs()) {
				return -1;
			}
			else if (getNumArgs() < oe.getNumArgs()) {
				return 1;
			}
			else {
				// sort by the first different argument
				for (int i=0; i<getNumArgs(); i++) {
					if (! getArg(i).equals(oe.getArg(i)))
						return getArg(i).compareTo(oe.getArg(i));
				}
			}
			
			// at this point all the arguments were the same, so give up
			// (this should be covered by the equality check up top)
			return 0;
		}
		// OperatorExpressions come before other types of expression
		else if(e instanceof VariableExpression || e instanceof NumberExpression) {
			return Integer.MIN_VALUE;   //TODO: apart from the appropriate harshness of the answer
			                            //given to the user for daring to compare an operator
			                            //expression to a variable or number expression,
			                            //is there another reason MIN_VALUE is used instead of -1?
		}
		else {
			//e wasn't a numberExpression, variableExpression, or operatorExpression
			//that's everything that should be considered by this method -- somebody 
			//passed something of the wrong class to this method!
			throw new ClassCastException();
		}
	}
	
	
	/**
	 * Simplifies the expression according to the simplify() method for the operator in question
	 * (i.e., it lets the simplify method of the Operator class do all the heavy lifting)
	 * @return the simplified expression
	 */
	public Expression simplify() {
		return op.simplify(this);
	}

}
