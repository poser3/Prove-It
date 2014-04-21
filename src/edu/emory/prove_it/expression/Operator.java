package edu.emory.prove_it.expression;
import java.util.ArrayList;
import java.util.Collections;

public class Operator implements Comparable<Operator> {
	
	////////////////////////
	// Instance Variables //
	////////////////////////
	
	private final String name;
	public final boolean isAssociative = false;
	private final String distributes = null;
	private final String inverse = null;
	
	//////////////////
	// Constructors //
	//////////////////
	
	/**
	 * Constructs an operator with a given name
	 * @param name the name of the operator in String form
	 */
	Operator(String name) {
		this.name = name;
	}
	
	
	///////////////////
	// Other Methods //
	///////////////////
	
	public short getPrecedence() {
		return 0;
	}
	
	public boolean isCommutative() {
		return false;
	}
	
	public boolean isAssociative() {
		return false;
	}
	
	public Operator getInverseOperator() {
		return null; 
	}
	
	/**
	 * Checks if this operator distributes over another.
	 * @param op
	 * @return true if this operator distributes over op, or false if it does not
	 */
	//TODO: What if an operator distributes over multiple other operators?
	//For example, multiplication distributes over addition AND subtraction.
	//The below seems to presume a given operator can only distribute over
	//one other operator.
	public boolean distributesOver(final Operator op) {
		String[] opStringsThisDistributesOver = opNamesThisDistributesOver().split(",");
		System.out.println(this.toString() + " distributes over " + opStringsThisDistributesOver);
		for (int i=0; i < opStringsThisDistributesOver.length; i++) {
			if (op.toString().equals(opStringsThisDistributesOver[i])) {
				System.out.println("checking " + op.toString() + " vs. " + opStringsThisDistributesOver[i]);
				return true;
			}
		}
		return false;
	}
	
	public String opNamesThisDistributesOver() {
		return distributes;
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
	 * The default implementation first compares the operator names, then compares the number 
	 * of arguments, and then compares each corresponding pair of arguments in order.
	 * @param e1 an OperatorExpression using this operator
	 * @param e2 another OperatorExpression using this operator
	 * @return true if e1 and e2 represent the same thing and false otherwise
	 */
	public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
		if ((! this.equals(e1.getOp()) || ! this.equals(e2.getOp())))
			return false;
		
		if (e1.getNumArgs() != e2.getNumArgs())
			return false;
		
		ArrayList<Expression> args1 = e1.getArgs();
		ArrayList<Expression> args2 = e2.getArgs();
		
		if (this.isCommutative()) {
			// Clone the argument lists because sorting happens in-place
			args1 = new ArrayList<Expression>(args1);
			args2 = new ArrayList<Expression>(args2);
			Collections.sort(args1);
			Collections.sort(args2);
		}
			
		for (int i=0; i<args1.size(); i++)
			if (! args1.get(i).equals(args2.get(i)))
				return false;
		
		return true;
	}
	
	/**
	 * Returns a string giving the name of this operator
	 * @return the name of this operator
	 */
	@Override
	public final String toString() {
		return name;
	}
	            
	/**
	 * A default way to convert an OperatorExpression using this operator into to LaTeX form.
	 * This method returns a single-line, parentheses heavy, and often non-ideal, infix notation for the expression.  
	 * This method should be overridden for most operators so that the LaTeX form used matches
	 * what students most often see. For example, if e is a unary minus and e.getArg(0) is a product
	 * of variables x and y, this method returns "- (x*y)", instead of the more desirable "-xy"       
	 * @param e an OperatorExpression using this operator
	 * @return the LaTeX representation of e
	 */
	public String toLatex(final OperatorExpression e) {
		
		//If the operator has no arguments, just use the operator's name...
		//TODO: Does an operator ever have zero arguments?  Doesn't it need something to operate on?
		if (e.getNumArgs() == 0)
			return e.getOp().name;
		
		//If the operator has one argument: 
		//use the form "op arg" when arg is not an operatorExpression, 
		//use the form "op (arg)" when arg is an operatorExpression.
		else if (e.getNumArgs() == 1) {
			StringBuilder sb = new StringBuilder();
			sb.append(e.getOp());
			sb.append(' ');
			sb.append(expressionWithParens(e.getArg(0)));
			return sb.toString();
		}
		
		//If the operator has more than one argument:
		//use the form "(arg) op (arg) op (arg) op ... op (arg)", 
		//leaving off the relevant parentheses when arg is not an operator expression 
		//or when arg is an operator expression whose operator has a higher precedence level
		
		//TODO:
		//With a strict inequality in the expressionWithParents() method, the expression (- a (+ b c)) 
		//should be displayed as a - (b + c), but instead gets displayed as a - b + c.  
		//Using a non-strict inequality in the expressionWithParens() method, the expression (+ a (+ b c)) 
		//ideally would be displayed as a + b + c, but instead, we see a + (b + c).  
		//Is the latter what we want? ...or is there a better way?  This problem shows up frequently
		//when using one of the substitute methods.  It may be that we want the parens, as shown in the
		//2nd example -- but we need a quick way to remove them when they aren't necessary, that doesn't 
		//do all the work for the student.
		
		else {
			StringBuilder sb = new StringBuilder();
			
			sb.append(expressionWithParens(e.getArg(0)));
			for (int i=1; i<e.getNumArgs(); i++) {
				sb.append(' ');
				sb.append(e.getOp());
				sb.append(' ');
				sb.append(expressionWithParens(e.getArg(i)));
            }
			return sb.toString();
		}
    }
	
	protected final String expressionWithParens(final Expression e) {
		String s = e.toLatex();
		if (e instanceof OperatorExpression && ((OperatorExpression) e).getOp().getPrecedence() <= this.getPrecedence())
			s = '(' + s + ')';
		return s;
	}
	
    /**
	 * Determines whether two operators are the same, based on their names.
	 * @param op
	 * @return true if this has the same name as op, or false otherwise
     */
	public final boolean equals(final Operator op) {
		return name.equals(op.toString());
	}
	
	/**
	 * Compares this operator to another. Operators are sorted ASCIIbetically by name.
	 */
	@Override
	public int compareTo(final Operator op) {
		return name.compareTo(op.toString());
	}
	
	/**
	 * Performs simplifications on an OperatorExpression using this operator.
	 * The default implementation just tries to simplify each argument.
	 * This should be overridden by some operators.  For example, if 
	 * e.getOp() is a "+", and any of its arguments are "0", they can be removed.
	 * If e.getOp() is a "*" and any of its arguments are "1", they can be removed.
	 * Note, the overriding methods might want to check the program settings (probably
	 * loaded at runtime from an appropriate file) to see if such simplifications
	 * should be made automatically.  For a calculus student throwing away any "+0"'s
	 * is a no-brainer, but for a pre-algebra student, this may not be the case. 
	 * @param e an OperatorExpression using this operator
	 * @return a simpler Expression with the same mathematical meaning
	 */
	public Expression simplify(final OperatorExpression e) {		//TODO: see note in javadoc about overriding this method
		ArrayList<Expression> args = new ArrayList<Expression>();
		for (Expression arg : e.getArgs()) {
			if (arg instanceof OperatorExpression)
				args.add(((OperatorExpression) arg).simplify());
			else
				args.add(e);
		}
		return new OperatorExpression(e.getOp(), args);
	}
	
	public Expression evaluate(final OperatorExpression e) {
		ArrayList<Expression> args = new ArrayList<Expression>();
		for (Expression arg : e.getArgs()) {
			if (arg instanceof OperatorExpression)
				args.add(((OperatorExpression) arg).evaluate());
			else
				args.add(e);
		}
		return new OperatorExpression(e.getOp(), args);
	}
	
	public Type getType(Type... argTypes) {
		return Type.LOGICAL;
	}

}
