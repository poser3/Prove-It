import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class Operator implements Comparable<Operator> {
	
	// Create a few default operators. Operators can be defined anywhere, but should all exist before any expressions are parsed.
	// This is the best place to define new operators, however.
	static {
		makeOperator("=", new OperatorSpec.CommutativeOperatorSpec(),
				"commutative");
		makeOperator("+", new OperatorSpec.CommutativeOperatorSpec() {
			public Expression simplify(final OperatorExpression e) {
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
					return new OperatorExpression(Operator.named("+"), args);
			}
		},
				"associative", "commutative", "inverse -");
		makeOperator("-", new OperatorSpec() {
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
						return args.get(1);
					else
						return new OperatorExpression(Operator.named("-"), args);
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
						return args.get(1);
					else
						return new OperatorExpression(Operator.named("-"), args);
				}
			}
		},
				"inverse +");
		makeOperator("*", new OperatorSpec.CommutativeOperatorSpec() {
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
					return new OperatorExpression(Operator.named("*"), args);
			}
		},
				"associative", "commutative", "distributes +", "inverse /");
		makeOperator("/", new OperatorSpec() {
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
							return args.get(1);
						else
							return new OperatorExpression(Operator.named("/"), args);
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
						return args.get(1);
					else
						return new OperatorExpression(Operator.named("/"), args);
				}
			}
		},
				"inverse *");
		makeOperator("^", new OperatorSpec() {
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
							BigDecimal value = ((NumberExpression) base).getValue().pow(((NumberExpression) power).getValue());
							return new NumberExpression(value);
						}
						catch (ArithmeticException exception) {
							// do nothing - other code handles the case where BigDecimal.pow() doesn't work
						}
					}
					if (base instanceof NumberExpression && ((NumberExpression) base).getValue().equals(BigDecimal.ZERO))
						return new NumberExpression(BigDecimal.ZERO); // If the power is negative this probably isn't the wisest course
					else if (power instanceof NumberExpression && ((NumberExpression) power).getValue().equals(BigDecimal.ONE))
						return base;
					else {
						ArrayList<Expression> args = new ArrayList<Expression>();
						args.add(base);
						args.add(power);
						return new OperatorExpression(Operator.named("^", args));
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
					return new OperatorExpression(Operator.named("^", args));
				}
			}
		},
				"distributes *");
		makeOperator("m", new OperatorSpec() {
			public String toLatex(OperatorExpression e) {
				return String.format("m %s",
						e.getArg(0).toLatex());
			}
		});
		makeOperator("congruent", new OperatorSpec.CommutativeOperatorSpec() {
			public String toLatex(OperatorExpression e) {
				return String.format("%s is congruent to %s",
						e.getArg(0).toLatex(), e.getArg(1).toLatex());
			}
		});
		makeOperator("between", new OperatorSpec() {
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (! e1.getArg(0).equals(e2.getArg(0)))
					return false;
				else
					return (e1.getArg(1).equals(e2.getArg(1)) && e1.getArg(2).equals(e2.getArg(2)))
							|| (e1.getArg(1).equals(e2.getArg(2)) && e1.getArg(2).equals(e2.getArg(1)));
			}
			
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is between %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
		makeOperator("line-on", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on line %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		makeOperator("ray-endpoint", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the endpoint of ray %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		makeOperator("ray-on", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on ray %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		makeOperator("circle-center", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is the center of circle %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		makeOperator("circle-on", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on circle %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		makeOperator("intersect", new OperatorSpec() {
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (e1.getArg(0) != e2.getArg(0))
					return false;
				return (e1.getArg(1).equals(e2.getArg(1)) && e1.getArg(2).equals(e2.getArg(2)))
						|| e1.getArg(1).equals(e2.getArg(2)) && e1.getArg(2).equals(e2.getArg(1));
			}
			
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the intersection of %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
		makeOperator("midpoint", new OperatorSpec() {
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the midpoint of %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
	}
	
	/**
	 * Stores name, operator pairs for lookup purposes.
	 * Initialized when needed in makeOperator.
	 * If you need an Operator, use Operator.named() instead.
	 */
	private static HashMap<String, Operator> ops;
	
	/**
	 * Returns the operator with the given name.
	 * @param name
	 * @return the operator named "name"
	 */
	public static Operator named(String name) {
		if(ops.containsKey(name))
			return ops.get(name);
		
		throw new IllegalArgumentException("Unrecognized operator \""+ name +"\"");
	}
	
	/**
	 * Creates a new Operator and registers it for access or use in parsing.
	 * @param name the operator's name as a String
	 * @param methods an OperatorSpec containing (possibly overridden) areEqual and toLaTeX methods for the operator
	 * @param properties several Strings representing properties of the operator:
	 * * "precedence n", where n is this operator's precedence
	 * * "associative", if this operator is associative
	 * * "commutative", if this operator is commutative
	 * * "distributes ", followed by a space-separated list of operators that this operator distributes over
	 * * "inverse ", followed by the name of an operator which is this operator's inverse
	 */
	public static void makeOperator(String name, OperatorSpec methods, String... properties) {
		if(ops == null)
			ops = new HashMap<String, Operator>();
		
		Operator op = new Operator(name, methods, properties);
		ops.put(name, op);
	}
	private Operator(String name, OperatorSpec methods, String... properties) {
		this.methods = methods == null ? new OperatorSpec() : methods;
		this.name = name;
		
		short precedence = 0;
		boolean isAssociative = false;
		boolean isCommutative = false;
		ArrayList<String> distributes = null;
		String inverse = null;
		
		for(int i=0; i<properties.length; i++) {
			if(properties[i].startsWith("precedence"))
				precedence = Short.parseShort(properties[i].substring(properties[i].lastIndexOf(' ')+1, properties[i].length()));
			else if(properties[i].startsWith("associative"))
				isAssociative = true;
			else if(properties[i].startsWith("commutative"))
				isCommutative = true;
			else if(properties[i].startsWith("distributes")) {
				distributes = new ArrayList<String>();
				String[] words = properties[i].split(" ");
				for(int j=1; j<words.length; j++)
					distributes.add(words[j]);
			}
			else if(properties[i].startsWith("inverse")) {
				inverse = properties[i].substring("inverse ".length());
			}
		}
		
		this.precedence = precedence;
		this.isAssociative = isAssociative;
		this.isCommutative = isCommutative;
		this.distributes = distributes;
		this.inverse = inverse;
	}
	
	public final String name;
	public final short precedence;
	public final boolean isAssociative;
	public final boolean isCommutative;
	private final ArrayList<String> distributes;
	private final String inverse;
	private final OperatorSpec methods;
	
	/**
	 * Checks if this operator distributes over another.
	 * @param op
	 * @return true if this operator distributes over op, or false if it does not
	 */
	public boolean distributesOver(Operator op) {
		if (distributes == null || distributes.size() == 0)
			return false;
		
		for(int i=0; i<distributes.size(); i++)
			if (distributes.get(i) == op.name)
				return true;
		
		return false;
	}
	/**
	 * Returns this operator's inverse, if it has one.
	 * @return this operator's inverse, or null if it doesn't have one
	 */
	public Operator inverse() {
		return inverse == null ? null : Operator.named(inverse);
	}
	/**
	 * Determines whether two OperatorExpressions represent the same thing.
	 * Calls the method provided in the OperatorSpec with which the operator was created.
	 * @param e1
	 * @param e2
	 * @return true if the OperatorExpressions represent the same thing, and false otherwise
	 */
	public boolean areEqual(OperatorExpression e1, OperatorExpression e2) {
		return equals(e1.getOp()) && equals(e2.getOp()) && methods.areEqual(e1, e2);
	}
	
	public final String toString() {
		return name;
	}
	/**
	 * Formats an OperatorExpression using this operator for use in a LaTex context.
	 * @param e an OperatorExpression using this operator
	 * @return a LaTex representation of the supplied expression
	 */
	public String toLatex(OperatorExpression e) {
		return methods.toLatex(e);
	}
	
	/**
	 * Determines whether two operators are the same, based on their names.
	 * @param op
	 * @return true if this has the same name as op, or false otherwise
	 */
	public final boolean equals(Operator op) {
		return name.equals(op.name);
	}
	/**
	 * Compares this operator to another. Operators are sorted ASCIIbetically by name.
	 */
	public int compareTo(Operator op) {
		return name.compareTo(op.name);
	}
	
	/**
	 * Simplify an OperatorExpression using this operator
	 */
	public Expression simplify(OperatorExpression e) {
		return methods.simplify(e);
	}
	
}