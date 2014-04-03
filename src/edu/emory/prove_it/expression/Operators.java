package edu.emory.prove_it.expression;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class Operators extends HashMap<String, Operator> {
	
	static final Operators ops = new Operators();
	public static Operator named(final String key) {
		return ops.get(key);
	}
	
	@SuppressWarnings("unused")
	private Operators() {
		put("=", new Operator("=") {
			public final boolean isCommutative = true;
			
			@Override
			public short getPrecedence() {
				return 1;
			}
			
		});
		put("!=", new Operator("!=") {
			public final boolean isCommutative = true;
			
			@Override
			public short getPrecedence() {
				return 1;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(expressionWithParens(e.getArg(0)));
				for(int i=1; i<e.getNumArgs(); i++) {
					sb.append(" \\neq ");
					sb.append(expressionWithParens(e.getArg(0)));
				}
				return sb.toString();
			}
		});
		put("<", new Operator("<") {
			@Override
			public short getPrecedence() {
				return 1;
			}
			
		});
		put(">", new Operator(">") {
			@Override
			public short getPrecedence() {
				return 1;
			}
			
		});
		put("<=", new Operator("<=") {

			@Override
			public short getPrecedence() {
				return 1;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(expressionWithParens(e.getArg(0)));
				for(int i=1; i<e.getNumArgs(); i++) {
					sb.append(" \\leq ");
					sb.append(expressionWithParens(e.getArg(i)));
				}
				return sb.toString();
			}
		});
		put(">=", new Operator(">=") {
			@Override
			public short getPrecedence() {
				return 1;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(expressionWithParens(e.getArg(0)));
				for(int i=1; i<e.getNumArgs(); i++) {
					sb.append(" \\geq ");
					sb.append(expressionWithParens(e.getArg(i)));
				}
				return sb.toString();
			}
		});
		put("+", new Operator("+") {
			public final boolean isCommutative = true;
			public final boolean isAssociative = true;
			private final String inverse = "-";
			
			@Override
			public short getPrecedence() {
				return 2;
			}
			
			@Override
			public Expression simplify(final OperatorExpression e) {
				
				//Keep a running total of constants present in the e.getArgs()
				//Simplify any of the args that are operatorExpressions
				//Then, put everything back together into a new expression,
				//keeping in mind the special cases that this could collapse 
				//all the way down to:
				// 1) nothing (sum of a bunch of zero args) and should be 0; or
				// 2) a number (sum of a bunch of constants) and should be a number expression

				BigDecimal constant = BigDecimal.ZERO;
				ArrayList<Expression> args = new ArrayList<Expression>();
				
				for(Expression arg : e.getArgs()) {
					if (arg instanceof NumberExpression)
						constant = constant.add(((NumberExpression) arg).getValue());
					else if (arg instanceof OperatorExpression)
						args.add(((OperatorExpression) arg).simplify());
					else
						args.add(arg);
				}				
				if (! constant.equals(BigDecimal.ZERO))
					args.add(new NumberExpression(constant));
				
				if (args.size() == 0)
					return new NumberExpression(BigDecimal.ZERO);
				else if (args.size() == 1)
					return args.get(0);
				else
					return new OperatorExpression(this, args);
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("-", new Operator("-") {
			private final String inverse = "+";
			
			@Override
			public short getPrecedence() {
				return 2;
			}
			
			@Override
			public Expression simplify(final OperatorExpression e) {
				if (e.getNumArgs() == 0)
					return e;
				else if (e.getNumArgs() == 1) {
					Expression arg = e.getArg(0);
					if (arg instanceof NumberExpression) {
						BigDecimal value = ((NumberExpression) arg).getValue();
						return new NumberExpression(value.multiply(new BigDecimal(-1)));
					}
					else if (arg instanceof OperatorExpression)
						return ((OperatorExpression) arg).simplify();
					else
						return e;
				}
				else if (e.getArg(0) instanceof NumberExpression) {
					BigDecimal constant = ((NumberExpression) e.getArg(0)).getValue();
					ArrayList<Expression> args = new ArrayList<Expression>();
					
					for (int i=1; i<e.getNumArgs(); i++) {
						if (e.getArg(i) instanceof NumberExpression)
							constant = constant.subtract(((NumberExpression) e.getArg(i)).getValue());
						else if (e.getArg(i) instanceof OperatorExpression)
							args.add(((OperatorExpression) e.getArg(i)).simplify());
						else
							args.add(e.getArg(i));
					}
					args.add(0, new NumberExpression(constant));
					
					if (args.size() == 1)
						return args.get(0);
					else
						return new OperatorExpression(this, args);
				}
				else {
					BigDecimal constant = BigDecimal.ZERO;
					ArrayList<Expression> args = new ArrayList<Expression>();
					args.add(e.getArg(0));
					
					for (int i=1; i<e.getNumArgs(); i++) {
						if (e.getArg(i) instanceof NumberExpression)
							constant = constant.subtract(((NumberExpression) e.getArg(i)).getValue());
						else
							args.add(e.getArg(i));
					}
					if (! constant.equals(BigDecimal.ZERO))
						args.add(new NumberExpression(constant));
					
					if (args.size() == 1)
						return args.get(0);
					else
						return new OperatorExpression(this, args);
				}
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("*", new Operator("*") {
			public final boolean isCommutative = true;
			public final boolean isAssociative = true;
			private final String distributes = "+";
			private final String inverse = "/";
			
			@Override
			public short getPrecedence() {
				return 3;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(expressionWithParens(e.getArg(0)));
				for(int i=1; i<e.getNumArgs(); i++) {
					sb.append(" \\cdot ");
					sb.append(expressionWithParens(e.getArg(i)));
				}
				return sb.toString();
			}
			
			@Override
			public Expression simplify(final OperatorExpression e) {
				BigDecimal constant = BigDecimal.ONE;
				ArrayList<Expression> args = new ArrayList<Expression>();
				
				for(Expression arg : e.getArgs()) {
					if (arg instanceof NumberExpression)
						constant = constant.multiply(((NumberExpression) arg).getValue());
					else if (arg instanceof OperatorExpression)
						args.add(((OperatorExpression) arg).simplify());
					else
						args.add(arg);
				}
				if (constant.equals(BigDecimal.ZERO))
					return new NumberExpression(BigDecimal.ZERO);
				else if (! constant.equals(BigDecimal.ONE))
					args.add(new NumberExpression(constant));
				
				if (args.size() == 0)
					return new NumberExpression(BigDecimal.ONE);
				else if (args.size() == 1)
					return args.get(0);
				else
					return new OperatorExpression(this, args);
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("/", new Operator("/") {
			private final String inverse = "*";
			
			@Override
			public short getPrecedence() {
				return 3;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(" \\frac{");
				sb.append(expressionWithParens(e.getArg(0)));
				sb.append("}{");
				sb.append(expressionWithParens(e.getArg(1)));
				sb.append("} ");
				return sb.toString();
			}
			
			@Override
			public Expression simplify(final OperatorExpression e) {
				if (e.getNumArgs() == 0)
					return e;
				else if (e.getNumArgs() == 1) {
					Expression arg = e.getArg(0);
					if (arg instanceof NumberExpression) {
						BigDecimal value = ((NumberExpression) arg).getValue();
						return new NumberExpression(BigDecimal.ONE.divide(value));
					}
					else if (arg instanceof OperatorExpression)
						return ((OperatorExpression) arg).simplify();
					else
						return e;
				}
				else if (e.getArg(0) instanceof NumberExpression) {
					BigDecimal constant = ((NumberExpression) e.getArg(0)).getValue();
					ArrayList<Expression> args = new ArrayList<Expression>();
					
					for (int i=1; i<e.getNumArgs(); i++) {
						if (e.getArg(i) instanceof NumberExpression)
							constant = constant.divide(((NumberExpression) e.getArg(i)).getValue());
						else if (e.getArg(i) instanceof OperatorExpression)
							args.add(((OperatorExpression) e.getArg(i)).simplify());
						else
							args.add(e.getArg(i));
					}
					if (constant.equals(BigDecimal.ZERO))
						return new NumberExpression(BigDecimal.ZERO);
					else {
						args.add(0, new NumberExpression(constant));
						if (args.size() == 1)
							return args.get(0);
						else
							return new OperatorExpression(this, args);
					}
				}
				else {
					BigDecimal constant = BigDecimal.ONE;
					ArrayList<Expression> args = new ArrayList<Expression>();
					args.add(e.getArg(0));
					
					for (int i=1; i<e.getNumArgs(); i++) {
						if (e.getArg(i) instanceof NumberExpression)
							constant = constant.divide(((NumberExpression) e.getArg(i)).getValue());
						else
							args.add(e.getArg(i));
					}
					if (! constant.equals(BigDecimal.ONE))
						args.add(new NumberExpression(constant));
					
					if (args.size() == 1)
						return args.get(0);
					else
						return new OperatorExpression(this, args);
				}
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("^", new Operator("^") {
			private final String distributes = "*";
			
			@Override
			public short getPrecedence() {
				return 4;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				sb.append(expressionWithParens(e.getArg(0)));
				sb.append("}^{");
				sb.append(expressionWithParens(e.getArg(1)));
				sb.append("} ");
				return sb.toString();
			}
			
			@Override
			public Expression simplify(final OperatorExpression e) {
				if (e.getNumArgs() == 2) {
					Expression base = e.getArg(0);
					Expression power = e.getArg(1);
					
					if (base instanceof OperatorExpression)
						base = ((OperatorExpression) base).simplify();
					if (power instanceof OperatorExpression)
						power = ((OperatorExpression) power).simplify();
					
					if (base instanceof NumberExpression && power instanceof NumberExpression) {
						try {
							BigDecimal value = ((NumberExpression) base).getValue().pow(((NumberExpression) power).getValue().intValueExact());
							return new NumberExpression(value);
						}
						catch (ArithmeticException exception) {
							// do nothing - other code handles the case where BigDecimal.pow() doesn't work
						}
					}
					if (base instanceof NumberExpression && ((NumberExpression) base).getValue().equals(BigDecimal.ZERO))
						return new NumberExpression(BigDecimal.ZERO); // If the power is negative this probably isn't the wisest course
					else if (power instanceof NumberExpression && ((NumberExpression) power).getValue().equals(BigDecimal.ZERO))
						return new NumberExpression(BigDecimal.ONE);
					else if (power instanceof NumberExpression && ((NumberExpression) power).getValue().equals(BigDecimal.ONE))
						return base;
					else {
						ArrayList<Expression> args = new ArrayList<Expression>();
						args.add(base);
						args.add(power);
						return new OperatorExpression(this, args);
					}
				}
				else {
					ArrayList<Expression> args = new ArrayList<Expression>(e.getNumArgs());
					for (Expression arg : e.getArgs()) {
						if (arg instanceof OperatorExpression)
							args.add(((OperatorExpression) arg).simplify());
						else
							args.add(arg);
					}
					return new OperatorExpression(this, args);
				}
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("m", new Operator("m") {
			@Override
			public short getPrecedence() {
				return 5;
			}
			
			@Override
			public String toLatex(OperatorExpression e) {
				return String.format("\\text{m } %s",
						expressionWithParens(e.getArg(0)));
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.NUMBER;
			}
		});
		put("angle", new Operator("angle") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (! e1.getArg(1).equals(e2.getArg(1)))
					return false;
				else
					return (e1.getArg(0).equals(e2.getArg(0)) && e1.getArg(2).equals(e2.getArg(2)))
							|| (e1.getArg(0).equals(e2.getArg(2)) && e1.getArg(2).equals(e2.getArg(0)));
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
					return String.format("\\angle %s %s %s",
									expressionWithParens(e.getArg(0)),
									expressionWithParens(e.getArg(1)),
									expressionWithParens(e.getArg(2)));
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.ANGLE;
			}
		});
		put("segment", new Operator("segment") {
			public final boolean isCommutative = true;
			
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
					return String.format("\\text{segment } \\overline{%s %s}",
									expressionWithParens(e.getArg(0)),
									expressionWithParens(e.getArg(1)));
			}
			
			@Override
			public Type getType(Type... argTypes) {
				return Type.SEGMENT;
			}
		});
		put("congruent", new Operator("congruent") {
			public final boolean isCommutative = true;
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
						
			@Override
			public String toLatex(OperatorExpression e) {
				return String.format("%s \\text{ is congruent to } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)));
			}
		});
		put("between", new Operator("between") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (! e1.getArg(0).equals(e2.getArg(0)))
					return false;
				else
					return (e1.getArg(1).equals(e2.getArg(1)) && e1.getArg(2).equals(e2.getArg(2)))
							|| (e1.getArg(1).equals(e2.getArg(2)) && e1.getArg(2).equals(e2.getArg(1)));
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ is between } %s \\text{ and } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)),
						expressionWithParens(e.getArg(2)));
			}
		});
		put("on", new Operator("on") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ is on } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)));
			}
		});
		put("endpoint", new Operator("endpoint") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ is the endpoint of } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)));
			}
		});
		put("center", new Operator("center") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ is the center of } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)));
			}
		});
		put("intersect", new Operator("intersect") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (e1.getArg(2) != e2.getArg(2))
					return false;
				return (e1.getArg(0).equals(e2.getArg(0)) && e1.getArg(1).equals(e2.getArg(1)))
						|| e1.getArg(0).equals(e2.getArg(1)) && e1.getArg(1).equals(e2.getArg(0));
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ and } %s \\text{ intersect at } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)),
						expressionWithParens(e.getArg(2)));
			}
		});
		put("midpoint", new Operator("midpoint") {
			@Override
			public short getPrecedence() {
				return Short.MIN_VALUE;
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s \\text{ is the midpoint of } %s \\text{ and } %s",
						expressionWithParens(e.getArg(0)),
						expressionWithParens(e.getArg(1)),
						expressionWithParens(e.getArg(2)));
			}
		});
	}

}
