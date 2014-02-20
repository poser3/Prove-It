import java.util.ArrayList;
import java.util.HashMap;

public abstract class Expression implements Comparable<Expression>, Selectable {
	
	////////////////////////
	// Instance Variables //
	////////////////////////
	
	private boolean isSelected_ = false;
	private String type_ = "unknown";
	
	////////////////////
	// Static Methods //
	////////////////////
	
	/**
	 * Turn an prefix LISP-like expression template into an Expression.
	 * @param template a String containing a prefix LISP-like expression
	 * @return the template in tree form
	 * @throws IllegalArgumentException if parentheses are mismatched or the operator was not recognized
	 */
	public static Expression parse(String template) {
		//example of a template string "= (^ c 2) (+ (^ a 2) (^ b 2))"
		
		//if template string is empty, there is no expression to create
		if (template.length() == 0)
			return null;
		
		//In the expression "+ a (^ x 2) (* y (+ z w)) u (- v 2)", we have words (one word per line):
		// +
		// a
		// ^ x 2
		// * y (+ z w)
		// u
		// - v 2
		// These words get stored in an arrayList named words
		ArrayList<String> words = new ArrayList<String>();
		
		//break template string into an array of chars
		char[] chs = template.toCharArray();
		
		//keep track of how deep into parentheses you are with parenLevel - initially 0
		//later, parenLevel = number of open parentheses - number of close parentheses
		short parenLevel = 0;
		
		//use a stringBuilder as temporary storage, as the word is being built up
		StringBuilder sb = new StringBuilder();
		
		//visit each character of the template string, in turn...
		for(int i=0; i<chs.length; i++) {
			
			//if you are in the middle of building a word, the first parenLevel 0 space will mark the end of the word
			//once this space has been found, add the word (as a string) to the words arrayList, and start over with
			//an empty stringBuilder...
			if(chs[i] == ' ' && parenLevel == 0 && sb.length() > 0) {
				words.add(sb.toString());
				sb = new StringBuilder();
			}
			
			//If you encounter parens, raise or lower the parenLevel appropriately.
			//Be on watch for mismatched parens, and throw an exception if this is seen.
			//Other than those things, add the chars encountered, as appropriate, to the word...
			else {
				if(chs[i] == '(')
					parenLevel++;
				if(parenLevel != 0 || (chs[i] != '(' && chs[i] != ')'))  //TODO: does the last part of this "or" make a difference?
					sb.append(chs[i]);
				if(chs[i] == ')') {
					parenLevel--;
					if(parenLevel < 0)  // i.e., if there were more closed parentheses than there were open ones...
						throw new IllegalArgumentException("Mismatched parentheses in template \""+ template +"\" near index "+ i);
				}
			}
		}
		
		// The last word doesn't get added in the for-loop above, so add it now.
		words.add(sb.toString());
		
		// Check one final time for mismatched parentheses...
		if(parenLevel != 0)
			throw new IllegalArgumentException("Mismatched parentheses in template \""+ template +"\"");
		
		// Now we figure out what we are looking at...
		
		// Note, numbers and variables should not have any spaces in their strings.  As such, if
		// there is only one word and it does not contain a space, we parse this as a number or variable 
		// (i.e., return a newly constructed numberExpression or variableExpression)
		if(words.size() == 1 && template.indexOf(' ') == -1) {
			try {
				// Parse words[0] as a number
				return new NumberExpression(words.get(0));
			}
			catch(NumberFormatException e) {
				// Just take it as a string
				
				// The variable name should take the form "type_name"
				// so we need to separate it into the type and name to use the constructor
				String varString = words.get(0);
				String varType = VariableExpression.getVarTypeFromString(varString);
				String varName = VariableExpression.getVarNameFromString(varString);
				
				return new VariableExpression(varType, varName);
				//return new VariableExpression(words.get(0));
			}
		}
		
		// If there is only one word and it does contain a space, then this is a single expression  
		// enclosed in an extra pair of parentheses. Hence, strip off the extra parentheses and try again.
		else if(words.size() == 1 && template.indexOf(' ') != -1) {
			return parse(words.get(0).substring(1, words.get(0).length()-1));
		}
		
		// If there are multiple words, the first one must be an operator (since template is in prefix form)
		// and we parse this as an OperatorExpression
		else {
			
			// identify the operator (i.e., the first word)
			Operator op = Operators.named(words.get(0));
			
			// create an arrayList for all of the arguments to the operator (i.e., the rest of the words)
			ArrayList<Expression> args = new ArrayList<Expression>(words.size()-1);
			for(int i=1; i<words.size(); i++) {
				String word = words.get(i);
				// Save ourselves some stack space for compound expressions in arguments
                                if(word.startsWith("\\(") && word.endsWith("\\)"))
					word = word.substring(1, word.length()-1);
				args.add(parse(word));
			}
			
			// Make and return the OperatorExpression mentioned above
			return new OperatorExpression(op, args);
		}
	}
	
	
	//////////////////////
	// Abstract Methods //
	//////////////////////
	
	/**
	 * Determine whether two expressions are the same.
	 * @param obj another object
	 * @return true if the two objects are the same, or false if they are not
	 */
	@Override
	public abstract boolean equals(Object obj);
	
	/**
	 * Return a string representation of this Expression.
	 * @return a string representation of this Expression
	 */
	@Override
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
	@Override
	public abstract int compareTo(Expression e);
	
	/////////////////////////
	// Getters and Setters //
	/////////////////////////
	
	public String getType() {
		return type_;
	}
	
	public void setType(String type) {
		type_ = type;
	}
	
	@Override
	public boolean isSelected() {
		return isSelected_;
	}
	
	@Override
	public void setSelected(boolean isSelected) {
		isSelected_ = isSelected;
	}
	
	public Expression getSelectedSubExpression() {
		if (this.isSelected_) {
			return this;
		}
		else if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			ArrayList<Expression> args = oe.getArgs();
			for (int i = 0; i < args.size(); i++) {
				if (args.get(i).isSelected()) {
					return args.get(i);
				}
				Expression e = args.get(i).getSelectedSubExpression();
				if (e != null) {
					return e;
				}
			}
		}
		return null;
	}
	
	public void deselectRecursive() {
		this.setSelected(false);
		if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			for (Expression arg : oe.getArgs()) {
				arg.deselectRecursive();
			}
		}
		else { //this is an instance of VariableExpression or NumberExpression
			this.setSelected(false);
		}
	}
	
	public void shiftSelectionRight() {
		if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			ArrayList<Expression> args = oe.getArgs();
			for (int i=0; i < args.size(); i++) {
				if ((args.get(i).isSelected()) && (i+1 < args.size())) {
					args.get(i).setSelected(false);
					args.get(i+1).setSelected(true);
					break; // i.e., don't look for another selected expression to shift
				}
				else if ((args.get(i).isSelected()) && (i+1 == args.size())) {
					args.get(i).setSelected(false);
					this.setSelected(true);
					break; 
				}
				else {
					args.get(i).shiftSelectionRight();
				}
			}
			
		}
	}
	
	public void shiftSelectionLeft() {
		if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			ArrayList<Expression> args = oe.getArgs();
			for (int i=0; i < args.size(); i++) {
				if ((args.get(i).isSelected()) && (i-1 >= 0)) {
					args.get(i).setSelected(false);
					args.get(i-1).setSelected(true);
					break;
				}
				else if ((args.get(i).isSelected()) && (i-1 < 0)) {
					args.get(i).setSelected(false);
					this.setSelected(true);
					break;
				}
				else {
					args.get(i).shiftSelectionLeft();
				}
			}
		}
	}
	
	public void shiftSelectionDeeper() {
		if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			ArrayList<Expression> args = oe.getArgs();
			if (this.isSelected() && (args.size() > 0)) {
				this.setSelected(false);
				args.get(0).setSelected(true);
			}
			else {
				for (int i=0; i < args.size(); i++) {
					args.get(i).shiftSelectionDeeper();
				}
			}
		}
	}
	
	public void shiftSelectionHigher() {
		if (this instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) this;
			ArrayList<Expression> args = oe.getArgs();
			boolean shiftedSomething = false;
			for (int i=0; i < args.size(); i++) {
				if (args.get(i).isSelected()) {
					args.get(i).setSelected(false);
					this.setSelected(true);
					shiftedSomething = true;
				}
			}
			if (!shiftedSomething) {
				for (int i=0; i < args.size(); i++) {
					args.get(i).shiftSelectionHigher();
				}
			}
		}
	}
	
	///////////////////
	// Other Methods //
	///////////////////
	
	/**
	 * Applies an operator to this, with another expression as its left argument
	 * @param op the operator to apply
	 * @param e the operator's left argument
	 * @return e op this
	 */
	public OperatorExpression applyLeft(Operator op, Expression e) {
		
		//if the operator is associative, we can minimize the depth of subexpressions needed...
		if (op.isAssociative) {
			
			// catch the case where op, this.op and e.op are the same
			// Example: suppose all of the operators are sums:
			//   this = "+ a b" 
			//   op = "+"
			//   e = "+ x y"
			// Then the result should be this = "+ x y a b" 
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far left as they can go
			if (this instanceof OperatorExpression && e instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				OperatorExpression oeE = (OperatorExpression) e;			
				if(oeThis.getOp().equals(op) && oeE.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeE.getArgs());
					newArgs.addAll(oeThis.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			
			// catch the case where op and this.op are the same
			// Example: suppose op and this.op are sums:
			//   this = "+ a b" 
			//   op = "+"
			//   e = "- x y"
			// Then the result should be this = "+ (- x y) a b "
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far left as they can go
			else if (this instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				if(oeThis.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.add(e);
					newArgs.addAll(oeThis.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			
			// catch the case where op and e.op are the same
			// Example: suppose op and e.op are sums:
			//   this = "- a b" 
			//   op = "+"
			//   e = "+ x y"
			// Then the result should be this = "+ x y (- a b)"
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far left as they can go
			else if (e instanceof OperatorExpression) {
				OperatorExpression oeE = (OperatorExpression) e;
				if(oeE.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeE.getArgs());
					newArgs.add(this);
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
		}
		
		// If we made it this far, we need to construct a binary expression 
		// in the usual way (use operator op, and arguments e and this -- keeping e to the left).
		// Example: 		
		//   this = "- a b" 
		//   op = "+"
		//   e = "* x y"
		// Then the result should be this = "+ (* x y) (- a b)"
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
		
		//if the operator is associative, we can minimize the depth of subexpressions needed...
		if(op.isAssociative) {
			
			// catch the case where op, this.op and e.op are the same
			// Example: suppose all of the operators are sums:
			//   this = "+ a b" 
			//   op = "+"
			//   e = "+ x y"
			// Then the result should be this = "+ a b x y" 
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far right as they can go
			if(this instanceof OperatorExpression && e instanceof OperatorExpression) {
				OperatorExpression oeThis = (OperatorExpression) this;
				OperatorExpression oeE = (OperatorExpression) e;			
				if(oeThis.getOp().equals(op) && oeE.getOp().equals(op)) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					newArgs.addAll(oeThis.getArgs());
					newArgs.addAll(oeE.getArgs());
					return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
				}
			}
			
			// catch the case where op and this.op are the same
			// Example: suppose op and this.op are sums:
			//   this = "+ a b" 
			//   op = "+"
			//   e = "- x y"
			// Then the result should be this = "+ a b (- x y)"
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far right as they can go
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
			// Example: suppose op and e.op are sums:
			//   this = "- a b" 
			//   op = "+"
			//   e = "+ x y"
			// Then the result should be this = "+ (- a b) x y"
			// Note, this only works with associative operators 
			// (e.g., this applies to "+" and "*",  but does not apply to "-" and "/")
			// Also note, x & y are placed as far right as they can go
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
		
		// If we made it this far, we need to construct a binary expression 
		// in the usual way (use operator op, and arguments this and e -- keeping e to the right).
		// Example: 		
		//   this = "- a b" 
		//   op = "+"
		//   e = "* x y"
		// Then the result should be this = "+ (- a b) (* x y)"
		ArrayList<Expression> newArgs = new ArrayList<Expression>();
		newArgs.add(this);
		newArgs.add(e);
		return (OperatorExpression) new OperatorExpression(op, newArgs).trim();
	}
	
	/**
	 * Create a new expression that is identical to this expression
	 * @return
	 */
    public Expression duplicate() {
		
		if (this instanceof OperatorExpression) {
			OperatorExpression oeThis = (OperatorExpression) this;
			ArrayList<Expression> args = new ArrayList<Expression>(oeThis.getNumArgs());
			for (int i=0; i < oeThis.getNumArgs(); i++) {
				args.add(oeThis.getArg(i).duplicate());
			}
			return new OperatorExpression(oeThis.getOp(), args).trim();
		}
		else if (this instanceof VariableExpression) {
			String varType = ((VariableExpression) this).getType();
			
			String variableLatex;
			if (this.isSelected()) {
				this.setSelected(false);
				variableLatex = this.toLatex();
				this.setSelected(true);
			}
			else {
				variableLatex = this.toLatex();
			}
			return new VariableExpression(varType, this.toString(), variableLatex);
		}
		else { // i.e., this is an instance of NumberExpression 
			return new NumberExpression(this.toString());
		}
	}
	
	/**
	 * Create a new expression that is identical to this expression, except that
	 * every occurrence of expression quid in this expression has been replaced 
	 * by the expression quo
	 * @param quid the expression to be replaced
	 * @param quo the expression to replace each occurrence of quid
	 * @return a new expression identical to this one, but with each occurrence of 
	 * quid replaced by quo
	 */
	public Expression substitute(Expression quid, Expression quo) {
		
		/* TODO:
		 * This builds a new OperatorExpression even if this contains no instances of quid,
		 * which I consider a design flaw but can't envision a way around.  L.V.
		 */
		
		if (this.equals(quid)) {
			return quo;
		}
		else if (this instanceof OperatorExpression) {
			OperatorExpression oeThis = (OperatorExpression) this;
			ArrayList<Expression> args = new ArrayList<Expression>(oeThis.getNumArgs());
			for (int i=0; i < oeThis.getNumArgs(); i++) {
				args.add(oeThis.getArg(i).substitute(quid, quo));
			}
			return new OperatorExpression(oeThis.getOp(), args).trim();
		}
		else
			return this;
	}
	
	/**
	 * Perform a given set of variable substitutions simultaneously.
	 * Example: Suppose 
	 *    map = { x1 -> x2;
	 *            y1 -> y2;
	 *            z1 -> z2 }
	 *    this = "= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"
	 * returns the expression "= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"
	 * @param map a HashMap whose keys are variable names to be replaced and whose values are the names that replace them
	 * @return this expression, after the given substitutions 
	 */
	public Expression substitute(HashMap<String, String> map) {
		
		if (this instanceof NumberExpression) {
			return this;
		}
		else if (this instanceof VariableExpression) {
			String name = ((VariableExpression) this).toString();
			if (map.containsKey(name)) {
				//added below to account for when map.get(name) was not a variable expression
				//i.e., a simple name, but instead a subexpression
				return Expression.parse(map.get(name));
				//return new VariableExpression(map.get(name));
			}
			return this;
		}
		else { // this is an instance of OperatorExpression
			Operator op = ((OperatorExpression) this).getOp();
			ArrayList<Expression> args = ((OperatorExpression) this).getArgs();
			ArrayList<Expression> newArgs = new ArrayList<Expression>();
			
			for(int i=0; i<args.size(); i++) 
				newArgs.add(args.get(i).substitute(map));
			
			return new OperatorExpression(op, newArgs).trim();
		}
	}
	
	/**
	 * Determine whether or not this expression matches a given LISP-like expression template (in prefix form).
	 * @param template a String containing a LISP-like expression (in prefix form)
	 * @return true if this expression matches the template, or false if it does not.
	 */
	public boolean matchesTemplate(String template) {
		return equals(parse(template));
	}
	
	/**
	 * Reconciles a template with an expression that are identical up to names of variables, creating a HashMap of 
	 * variable names that correspond to one another in the two expressions
	 * Example:  Suppose
	 *    this = "= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"
	 *    that = "= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"
	 *    returns hashMap = { "x1" -> "x2";
	 *	                      "y1" -> "y2";
	 * 	                      "z1" -> "z2" }
	 * @param template a prefix LISP-like expression template representing an expression for reconciliation
	 * @return a map from variable names in this expression to variable names used by the template containing the substitutions necessary for reconciliation, or null if no reconciliation is possible.
	 */
	public HashMap<String, String> findPairings(String template) {
		return findPairings(parse(template));
	}
	
	/**
	 * Reconciles expressions that are identical up to names of variables, creating a HashMap of 
	 * variable names that correspond to one another in the two expressions
	 * Example:  Suppose
	 *    this = "= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"
	 *    that = "= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"
	 *    returns hashMap = { "x1" -> "x2";
	 *	                      "y1" -> "y2";
	 *	                      "z1" -> "z2" }
	 * @param that another expression
	 * @return a HashMap from names of variables in this to names of variables in that.
	 * Performing the substitutions which this map represents would make this and that equal.
	 * If this and that cannot be reconciled, the return value is null.
	 */
	public HashMap<String, String> findPairings(Expression that) {
		
		if (this instanceof NumberExpression) {
			return equals(that) ? new HashMap<String, String>() : null;
		}
		else if (this instanceof VariableExpression) {
			if (equals(that)) {
				return new HashMap<String, String>();
			}
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
						if(map.containsKey(quid) && (map.get(quid) != subMap.get(quid)))
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
	 * This should probably be deprecated, as it could be downright harmful if expressions become mutable.
	 * Currently used in the following methods:
	 *    applyLeft(Operator op, Expression e) 
	 *    applyRight(Operator op, Expression e) 
	 *    substitute(Expression quid, Expression quo), and 
	 *    substitute(HashMap<String, String> map)
	 */
	public Expression trim() {
		//if this is not an operatorExpression, there is nothing that can be trimmed, so
		if (this instanceof OperatorExpression) {
			Trimmer.iterate((OperatorExpression) this);
		}
		return this;
	}
	
	////////////////////
	// Nested Classes //
	////////////////////
	
	/**
	 * Static nested class for use by the trim() method.
	 * @author Lee Vian
	 */
	private static class Trimmer {
		/**
		 * Maintains a list of references to expressions already encountered during the execution of this program
		 * (not just those seen in one expression).  
		 */
		private static ArrayList<Expression> expressions = new ArrayList<Expression>();
		
		/**
		 * Determines whether an expression has already been encountered.
		 * @param e an expression
		 * @return the index of the expression in the expression list if it has been encountered, 
		 * or -1 if it has not been encountered
		 */
		private static int alreadySeen(Expression e) {
			for (int i = 0; i < expressions.size(); i++) {
				if (expressions.get(i).equals(e)) {
					return i;
				}
			}
			
			return -1;
		}
		
		/**
		 * Look through each argument to an OperatorExpression, visit those that have not been visited, 
		 * and replace those that have been visited.
		 * @param e an OperatorExpression to look through
		 */
		static void iterate(OperatorExpression e) {
			// for each argument
			for (int i = 0; i < e.getNumArgs(); i++) {
				
				Expression arg = e.getArg(i);
				
				if (alreadySeen(arg) >= 0) {
					// If this argument has been visited, replace it with the first incarnation
					e.getArgs().set(i, expressions.get(alreadySeen(arg)));
				}
				else {
					// Visit it
					expressions.add(arg);
					if (arg instanceof OperatorExpression) {
						// If it has subexpressions, visit each of them
						iterate((OperatorExpression) arg);
					}
				}
			}
		}
	}
	
}
