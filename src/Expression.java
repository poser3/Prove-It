import java.util.ArrayList;
import java.util.HashMap;

public abstract class Expression implements Comparable<Expression> {
	
	private boolean hidden = false;
	private final ArrayList<Expression> dependencies = new ArrayList<Expression>();
	
	/**
	 * Turn an prefix LISP-like expression template into an Expression.
	 * @param template a String containing a prefix LISP-like expression
	 * @return the template in tree form
	 * @throws IllegalArgumentException if parentheses are mismatched or the operator was not recognized
	 */
	public static Expression parse(String template) {
		//example of a template string "= (^ c 2) (+ (^ a 2) (^ b 2))"
		if (template.length() == 0)
			return null;
		ArrayList<String> words = new ArrayList<String>();
		char[] chs = template.toCharArray();
		
		short parenLevel = 0; // parenLevel = number of open parentheses - number of close parentheses
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<chs.length; i++) {
			if(chs[i] == ' ' && parenLevel == 0 && sb.length() > 0) {
				// A space outside of parentheses is the word separator
				words.add(sb.toString());
				sb = new StringBuilder();
			}
			else {
				if(chs[i] == '(')
					parenLevel++;
				if(parenLevel != 0 || (chs[i] != '(' && chs[i] != ')'))
					sb.append(chs[i]);
				if(chs[i] == ')') {
					parenLevel--;
					// This means there were more close than open parentheses
					if(parenLevel < 0)
						throw new IllegalArgumentException("Mismatched parentheses in template \""+ template +"\" near index "+ i);
				}
			}
		}
		// Finish adding the last word
		words.add(sb.toString());
		if(parenLevel != 0)
			throw new IllegalArgumentException("Mismatched parentheses in template \""+ template +"\"");
		
		// If there is only one word, parse this as a number or variable
		if(words.size() == 1 && template.indexOf(' ') == -1) {
			try {
				// Parse words[0] as a number
				return new NumberExpression(words.get(0));
			}
			catch(NumberFormatException e) {
				// Just take it as a string
				return new VariableExpression(words.get(0));
			}
		}
		// If there is only one word and it contains a space, this is a single expression enclosed in an extra pair of parentheses
		else if(words.size() == 1 && template.indexOf(' ') != -1) {
			return parse(words.get(0).substring(1, words.get(0).length()-1));
		}
		// If there are multiple words, parse this as an OperatorExpression
		else {
			Operator op = Operator.named(words.get(0));
			ArrayList<Expression> args = new ArrayList<Expression>(words.size()-1);
			
			// Parse each argument to the operator and make a list of them
			for(int i=1; i<words.size(); i++) {
				String word = words.get(i);
				// Save ourselves some stack space for compound expressions in arguments
				if(word.startsWith("\\(") && word.endsWith("\\)"))
					word = word.substring(1, word.length()-1);
				args.add(parse(word));
			}
			
			// Make and return the expression
			return new OperatorExpression(op, args);
		}
	}
	
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public ArrayList<Expression> getDependencies() {
		return dependencies;
	}
	
	/**
	 * Determine whether two expressions are the same.
	 * @param e another expression
	 * @return true if the two expressions are the same, or false if they are not
	 */
	public abstract boolean equals(Expression e);
	
	/**
	 * Return a string representation of this Expression.
	 * @return a string representation of this Expression
	 */
	public abstract String toString();
	
	/**
	 * Return a LaTex representation of this Expression.
	 * @return a LaTex representation of this Expression
	 */
	public abstract String toLatex();
	
	/**
	 * Compare two expressions, for sorting purposes.
	 * OperatorExpressions come before VariableExpressions, which come before NumberExpressions.
	 * @param e another expression
	 * @return a negative integer if e comes before this, a positive number if e comes after this, or 0 if this and e are the same
	 */
	public abstract int compareTo(Expression e);
	
	/**
	 * Applies an operator to this, with another expression as its left argument
	 * @param op the operator to apply
	 * @param e the operator's left argument
	 * @return e op this
	 */
	public OperatorExpression applyLeft(Operator op, Expression e) {
		if(op.isAssociative) {
			// catch the case where op, this.op and e.op are the same
			if(this instanceof OperatorExpression && e instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				OperatorExpression oeE = (OperatorExpression) e;			
				if(oeThis.getOp().equals(op) && oeE.getOp().equals(op) && op.isAssociative) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeE.getArgs());
					newArgs.addAll(oeThis.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			// catch the case where op and this.op are the same
			else if(this instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				if(oeThis.getOp().equals(op) && op.isAssociative) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.add(e);
					newArgs.addAll(oeThis.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			// catch the case where op and e.op are the same
			else if(e instanceof OperatorExpression) {
				OperatorExpression oeE = (OperatorExpression) e;
				if(oeE.getOp().equals(op) && op.isAssociative) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeE.getArgs());
					newArgs.add(this);
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
		}
		
		ArrayList<Expression> newArgs = new ArrayList<Expression>();
		newArgs.add(e);
		newArgs.add(this);
		return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
	}
	
	/**
	 * Applies an operator to this, with another expression as its right argument
	 * @param op the operator to apply
	 * @param e the operator's right argument
	 * @return this op e
	 */
	public OperatorExpression applyRight(Operator op, Expression e) {
		if(op.isAssociative) {
			// catch the case where op, this.op and e.op are the same
			if(this instanceof OperatorExpression && e instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				OperatorExpression oeE = (OperatorExpression) e;			
				if(oeThis.getOp().equals(op) && oeE.getOp().equals(op) && op.isAssociative) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeThis.getArgs());
					newArgs.addAll(oeE.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			// catch the case where op and this.op are the same
			else if(this instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				if(oeThis.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeThis.getArgs());
					newArgs.add(e);
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			// catch the case where op and e.op are the same
			else if(e instanceof OperatorExpression) {
				OperatorExpression oeE = (OperatorExpression) e;
				if(oeE.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.add(this);
					newArgs.addAll(oeE.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
		}
		
		ArrayList<Expression> newArgs = new ArrayList<Expression>();
		newArgs.add(this);
		newArgs.add(e);
		return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
	}
	
	/**
	 * Replace each occurrence of one expression with another expression.
	 * @param quid the expression to be replaced
	 * @param quo the expression to replace each occurrence of quid
	 * @return this expression, with each occurrence of quid replaced by quo
	 */
	public Expression substitute(Expression quid, Expression quo) {
		if(equals(quid))
			return quo;
		/* 
		 * This builds a new OperatorExpression even if this contains no instances of quid,
		 * which I consider a design flaw but can't envision a way around.
		 */
		else if(this instanceof OperatorExpression) {
			OperatorExpression oeThis = (OperatorExpression) this;
			ArrayList<Expression> args = new ArrayList<Expression>(oeThis.getNumArgs());
			for(int i=0; i<oeThis.getNumArgs(); i++)
				args.add(oeThis.getArg(i).substitute(quid, quo));
			return new OperatorExpression(oeThis.getOp(), args).trim();
		}
		else
			return this;
	}
	/**
	 * Perform a given set of variable substitutions.
	 * The substitutions are performed simultaneously.
	 * @param map a HashMap whose keys are variable names to be replaced and whose values are the names that replace them
	 * @return this expression, after the given substitutions 
	 */
	public Expression substitute(HashMap<String, String> map) {
		if(this instanceof NumberExpression)
			return this;
		else if(this instanceof VariableExpression) {
			String name = ((VariableExpression) this).toString();
			if(map.containsKey(name))
				return new VariableExpression(map.get(name));
			
			return this;
		}
		else { // OperatorExpression
			Operator op = ((OperatorExpression) this).getOp();
			ArrayList<Expression> args = ((OperatorExpression) this).getArgs();
			ArrayList<Expression> newArgs = new ArrayList<Expression>();
			
			for(int i=0; i<args.size(); i++) 
				newArgs.add(args.get(i).substitute(map));
			
			return new OperatorExpression(op, newArgs).trim();
		}
	}
	
	/**
	 * Determine whether this expression matches a prefix LISP-like expression template.
	 * @param template a String containing a prefix LISP-like expression
	 * @return true if this expression matches the template, or false if it does not.
	 */
	public boolean matchesTemplate(String template) {
		return equals(parse(template));
	}
	
	/**
	 * Reconciles an expression with a template that is identical up to the names of variables.
	 * @param template a prefix LISP-like expression template representing an expression for reconciliation
	 * @return a map from variable names in this expression to variable names used by the template containing the substitutions necessary for reconciliation, or null if no reconciliation is possible.
	 */
	public HashMap<String, String> findPairings(String template) {
		return findPairings(parse(template));
	}
	/**
	 * Reconciles expressions that are identical up to names of variables.
	 * @param that another expression
	 * @return a HashMap from names of variables in this to names of variables in that.
	 * Performing the substitutions which this map represents would make this and that equal.
	 * If this and that cannot be reconciled, the return value is null.
	 */
	public HashMap<String, String> findPairings(Expression that) {
		if(this instanceof NumberExpression) {
			return equals(that) ? new HashMap<String, String>() : null;
		}
		else if(this instanceof VariableExpression) {
			if(equals(that))
				return new HashMap<String, String>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(((VariableExpression) this).toString(), ((VariableExpression) that).toString());
			return map;
		}
		else if(this instanceof OperatorExpression) {
			if(that instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				OperatorExpression oeThat = (OperatorExpression) that;
				if(! oeThis.getOp().equals(oeThat.getOp()) || oeThis.getNumArgs() != oeThat.getNumArgs())
					return null;
				
				ArrayList<HashMap<String, String>> subMaps = new ArrayList<HashMap<String, String>>();
				for(int i=0; i<oeThis.getNumArgs(); i++)
					subMaps.add(oeThis.getArg(i).findPairings(oeThat.getArg(i)));
				
				HashMap<String, String> map = new HashMap<String, String>();
				for(HashMap<String, String> subMap : subMaps) {
					for(String quid : subMap.keySet()) {
						if(map.containsKey(quid) && map.get(quid) != subMap.get(quid))
							return null;
						
						map.put(quid, subMap.get(quid));
					}
				}
				return map;
			}
			return null;
		}
		else
			throw new ClassCastException();
	}
	
	/**
	 * Saves memory by finding identical subexpressions within this expression and making them the same object.
	 * This is probably deprecated, and might be downright harmful if expressions become mutable.
	 */
	public Expression trim() {
		if(this instanceof OperatorExpression)
			Trimmer.iterate((OperatorExpression) this);
		return this;
	}
	
	/**
	 * Nested class for use by the trim() method.
	 * @author Lee Vian
	 */
	private static class Trimmer {
		/**
		 * Maintains a list of references to expressions already encountered.
		 */
		private static ArrayList<Expression> expressions = new ArrayList<Expression>();
		
		/**
		 * Determines whether an expression has already been encountered.
		 * @param e an expression
		 * @return the index of the expression in the expression list if it has been encountered, or -1 if it has not been encountered
		 */
		private static int alreadySeen(Expression e) {
			for(int i=0; i<expressions.size(); i++)
				if(expressions.get(i).equals(e))
					return i;
			return -1;
		}
		
		/**
		 * Look through each argument to an OperatorExpression, visit those that have not been visited, and replace those that have been visited.
		 * @param e an OperatorExpression to look through
		 */
		static void iterate(OperatorExpression e) {
			// for each argument
			for(int i=0; i<e.getNumArgs(); i++) {
				Expression arg = e.getArg(i);
				
				if(alreadySeen(arg) >= 0)
					// If this argument has been visited, replace it with the first incarnation
					e.getArgs().set(i, expressions.get(alreadySeen(arg)));
				else {
					// Visit it
					expressions.add(arg);
					if(arg instanceof OperatorExpression)
						// If it has subexpressions, visit each of them
						iterate((OperatorExpression) arg);
				}
			}
		}
	}
	
}
