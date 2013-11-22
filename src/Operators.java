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
		put("=", new Operator.CommutativeOperator("="));
		put("+", new Operator.CommutativeOperator("+") {
			public final boolean isAssociative = true;
			private final String inverse = "-";
			
			@Override
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
					return new OperatorExpression(this, args);
			}
		});
		put("-", new Operator("-") {
			private final String inverse = "+";
			
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
		});
		put("*", new Operator.CommutativeOperator("*") {
			public final boolean isAssociative = true;
			private final String distributes = "+";
			private final String inverse = "/";
			
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
		});
		put("/", new Operator("/") {
			private final String inverse = "*";
			
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
		});
		put("^", new Operator("^") {
			private final String distributes = "*";
			
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
		});
		put("m", new Operator("m") {
			@Override
			public String toLatex(OperatorExpression e) {
				return String.format("m %s",
						e.getArg(0).toLatex());
			}
		});
		put("congruent", new Operator.CommutativeOperator("congruent") {
			@Override
			public String toLatex(OperatorExpression e) {
				return String.format("%s is congruent to %s",
						e.getArg(0).toLatex(), e.getArg(1).toLatex());
			}
		});
		put("between", new Operator("between") {
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
				return String.format("%s is between %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
		put("angle", new Operator("angle") {
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
                    return String.format("angle %s-%s-%s",
                                    e.getArg(0).toLatex(),
                                    e.getArg(1).toLatex(),
                                    e.getArg(2).toLatex());
            }
	    });
	    put("segment", new Operator.CommutativeOperator("segment") {
	    	@Override
            public String toLatex(final OperatorExpression e) {
                    return String.format("segment %s-%s",
                                    e.getArg(0).toLatex(),
                                    e.getArg(1).toLatex());
            }
	    });
	    put("line", new Operator("line") {
	    	@Override
            public String toLatex(final OperatorExpression e) {
                    return String.format("line %s",
                                    e.getArg(0).toLatex());
            }
	    });
	    put("line-on", new Operator("line-on") {
	    	@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on line %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		put("ray", new Operator("ray") {
			@Override
			public String toLatex(final OperatorExpression e) {
                    return String.format("ray %s",
                                    e.getArg(0).toLatex());
            }
		});
		put("ray-endpoint", new Operator("ray-endpoint") {
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the endpoint of ray %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		put("ray-on", new Operator("ray-on") {
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on ray %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		put("circle", new Operator("circle") {
			@Override
			public String toLatex(final OperatorExpression e) {
                    return String.format("circle %s",
                                    e.getArg(0).toLatex());
            }
		});
		put("circle-center", new Operator("circle-center") {
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is the center of circle %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		put("circle-on", new Operator("circle-on") {
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("point %s is on circle %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex());
			}
		});
		put("intersect", new Operator("intersect") {
			@Override
			public boolean areEqual(final OperatorExpression e1, final OperatorExpression e2) {
				if (e1.getArg(0) != e2.getArg(0))
					return false;
				return (e1.getArg(1).equals(e2.getArg(1)) && e1.getArg(2).equals(e2.getArg(2)))
						|| e1.getArg(1).equals(e2.getArg(2)) && e1.getArg(2).equals(e2.getArg(1));
			}
			
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the intersection of %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
		put("midpoint", new Operator("midpoint") {
			@Override
			public String toLatex(final OperatorExpression e) {
				return String.format("%s is the midpoint of %s and %s",
						e.getArg(0).toLatex(),
						e.getArg(1).toLatex(),
						e.getArg(2).toLatex());
			}
		});
	}

}
