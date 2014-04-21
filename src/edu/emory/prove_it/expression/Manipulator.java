package edu.emory.prove_it.expression;

import java.util.ArrayList;

public class Manipulator {
	
	public static int NUM_ARGS_FOR_BINARY_OPERATOR = 2;
	
	public static Expression cancelCommonFactor(Expression e, OperatorExpression parent, OperatorExpression grandParent, VariableEnvironment variableEnvironment) {
		
		OperatorExpression p = (OperatorExpression) parent.duplicate();
		OperatorExpression gp = (OperatorExpression) grandParent.duplicate();
		boolean hasParent = (p != null);
		boolean hasGrandparent = (gp != null);
		boolean parentIsDivision = p.getOp().equals(Operators.named("/"));
		boolean parentIsProduct = p.getOp().equals(Operators.named("*"));
		boolean grandParentIsDivision = gp.getOp().equals(Operators.named("/"));
					
		//******* Case 1 **********//
	    //* quotient has form e/e *//
		//*************************//
		
		if ( (hasParent) &&
			 (parentIsDivision) &&
			 (p.getArg(0).equals(e)) && 
			 (p.getArg(1).equals(e))) { 
			return Expression.parse("1", variableEnvironment);
		}
		
		//**************** Case 2 *****************//
		//* quotient has form e / (a * e * b * c) *//
		//* and e in top expression is selected   *//
		//*****************************************//
		else if ( (hasParent) && 
				  (parentIsDivision) &&
				  (p.getArg(0).equals(e)) && 
			      (p.getArg(1) instanceof OperatorExpression) && 
			      (((OperatorExpression) p.getArg(1)).getOp().equals(Operators.named("*")))) { //i.e, case 2
			ArrayList<Expression> denominatorProductArgs = ((OperatorExpression) p.getArg(1)).getArgs();
			for (int i = 0; i < denominatorProductArgs.size(); i++) {
				if (denominatorProductArgs.get(i).equals(e)) {
					denominatorProductArgs.remove(i);
					Expression newDenominator = null;
					if (denominatorProductArgs.size() > 1) {
						newDenominator = new OperatorExpression("*",denominatorProductArgs);
					}
					else {
						newDenominator = denominatorProductArgs.get(0);
					}
					Expression newNumerator = Expression.parse("1", variableEnvironment);
					System.out.println(newDenominator.toString());
					if (newDenominator.equals(Expression.parse("1",variableEnvironment))) {
						System.out.println("new denominator was a 1");
						return newNumerator;
					}
					else {
						System.out.println("new denominator was not a 1");
						ArrayList<Expression> newQuotientArgs = new ArrayList<Expression>();
						newQuotientArgs.add(newNumerator);
						newQuotientArgs.add(newDenominator);
						return new OperatorExpression("/",newQuotientArgs);
					}
				}
			}	
		}
		
		//**************** Case 3 ******************//
		//* quotient has form (a * e * b * c) / e  *//
		//* and e in bottom expression is selected *//
		//******************************************//
		else if ( (hasParent) &&
				  (parentIsDivision) &&
				  (p.getArg(1).equals(e)) &&
				  (p.getArg(0) instanceof OperatorExpression) &&
				  (((OperatorExpression) p.getArg(0)).getOp().equals(Operators.named("*")))) { 
			ArrayList<Expression> numeratorProductArgs = ((OperatorExpression) p.getArg(0)).getArgs();
			for (int i = 0; i < numeratorProductArgs.size(); i++) {
				if (numeratorProductArgs.get(i).equals(e)) {
					numeratorProductArgs.remove(i);
					Expression newNumerator = null;
					if (numeratorProductArgs.size() > 1) {
						newNumerator = new OperatorExpression("*",numeratorProductArgs);
					}
					else {
						newNumerator = numeratorProductArgs.get(0);
					}
					Expression newDenominator = Expression.parse("1", variableEnvironment);
					System.out.println(newDenominator.toString());
					if (newDenominator.equals(Expression.parse("1",variableEnvironment))) {
						System.out.println("new denominator was a 1");
						return newNumerator;
					}
					else {
						System.out.println("new denominator was not a 1");
						ArrayList<Expression> newQuotientArgs = new ArrayList<Expression>();
						newQuotientArgs.add(newNumerator);
						newQuotientArgs.add(newDenominator);
						return new OperatorExpression("/",newQuotientArgs);
					}
				}
			}
		}
		
		//**************** Case 4 ******************//
		//* quotient has form (a * e * b * c) / e  *//
		//* and e in bottom expression is selected *//
		//******************************************//
		
		
		//************************ Case 5 ***********************//
		//* quotient has form (a * e * b * c) / (d * e * f * g) *//
		//*******************************************************//
		else if ( (hasParent) &&
				  (parentIsProduct) &&
				  (hasGrandparent) &&
				  (grandParentIsDivision) &&
				  (gp.getArg(0) instanceof OperatorExpression) &&
				  (((OperatorExpression) gp.getArg(0)).getOp().equals(Operators.named("*"))) &&
				  (gp.getArg(1) instanceof OperatorExpression) &&
				  (((OperatorExpression) gp.getArg(1)).getOp().equals(Operators.named("*"))) ) {
			ArrayList<Expression> numeratorProductArgs = ((OperatorExpression) gp.getArg(0)).getArgs();
			ArrayList<Expression> denominatorProductArgs = ((OperatorExpression) gp.getArg(1)).getArgs();
			
			Expression newNumerator = null;
			for (int i = 0; i < numeratorProductArgs.size(); i++) {
				System.out.println("checking to see if factor " + numeratorProductArgs.get(i) + " in numerator equals " + e.toString());
				if (numeratorProductArgs.get(i).equals(e)) {
					System.out.println("found it! will now remove it");
					numeratorProductArgs.remove(i);
					
					if (numeratorProductArgs.size() > 1) {
						newNumerator = new OperatorExpression("*",numeratorProductArgs);
					}
					else {
						newNumerator = numeratorProductArgs.get(0);
					}
					System.out.println("new numerator: " + newNumerator);
				}
			}
			
			Expression newDenominator = null;
			for (int i = 0; i < denominatorProductArgs.size(); i++) {
				System.out.println("checking to see if factor " + denominatorProductArgs.get(i) + " in denominator equals " + e.toString());
				if (denominatorProductArgs.get(i).equals(e)) {
					System.out.println("found it! will now remove it");
					denominatorProductArgs.remove(i);
					if (denominatorProductArgs.size() > 1) {
						newDenominator = new OperatorExpression("*",denominatorProductArgs);
					}
					else {
						newDenominator = denominatorProductArgs.get(0);
					}
					System.out.println("new denominator: " + newDenominator);
				}
			}
			if ( (newNumerator != null) && (newDenominator != null) ) {
				System.out.println(newDenominator.toString());
				if (newDenominator.equals(Expression.parse("1",variableEnvironment))) {
					System.out.println("new denominator was a 1");
					return newNumerator;
				}
				else {
					System.out.println("new denominator was not a 1");
					ArrayList<Expression> newQuotientArgs = new ArrayList<Expression>();
					newQuotientArgs.add(newNumerator);
					newQuotientArgs.add(newDenominator);
					return new OperatorExpression("/",newQuotientArgs);
				}
			}
		}
			
		//if you get this far, there was a problem
		return null;
	}
	
	public static Expression applyDefinitionOfDivision(Expression e, VariableEnvironment variableEnvironment) {
		OperatorExpression oe;
		System.out.println("Entered divisionIntoProduct()");
		if (e instanceof OperatorExpression) {
			oe = (OperatorExpression) e;
			Operator op = oe.getOp();
			if (op.equals(Operators.named("/"))) {
				ArrayList<Expression> newDivisionArgs = new ArrayList<Expression>();
				newDivisionArgs.add(Expression.parse("1",variableEnvironment));
				newDivisionArgs.add(oe.getArg(1));
				OperatorExpression reciprical = new OperatorExpression(Operators.named("/"),newDivisionArgs);
				ArrayList<Expression> newProductArgs = new ArrayList<Expression>();
				newProductArgs.add(oe.getArg(0));
				newProductArgs.add(reciprical);
				return new OperatorExpression(Operators.named("*"),newProductArgs);
			}
		}
		//if you get this far, there was a problem
		return null;
	}
	
	public static Expression dropParensOnSum(Expression e) {
		Expression result = null;
		OperatorExpression oe;
		System.out.println("Entered dropParensOnSum()");
		if (e instanceof OperatorExpression) {
			oe = (OperatorExpression) e;
			int numArgs = oe.getNumArgs();
			Operator op = oe.getOp();
			System.out.println("operator found: " + op);
			if (op.isAssociative()) {
				ArrayList<Expression> newArgs = new ArrayList<Expression>();
				for (int i=0; i < numArgs; i++) {
					Expression arg = oe.getArg(i);
					System.out.println(arg.toString() + " found");
					if (arg instanceof OperatorExpression) {
						OperatorExpression argOe = (OperatorExpression) arg;
						Operator argOp = argOe.getOp();
						int numArgArgs = argOe.getNumArgs();						
						if (argOp.equals(op)) { 
							for (int j=0; j < numArgArgs; j++) {
								System.out.println(argOe.getArg(j).toString() + " added");
								newArgs.add(argOe.getArg(j));
							}
						}
						else {
							newArgs.add(arg);
						}
					}
					else { //arg is not an operator expression, so just add it
						newArgs.add(arg);
					}
				}
				result = (new OperatorExpression(op,newArgs)).duplicate();
				return result;
			}
		}
		
		//if you get this far, the parens could not be dropped
		return null;
	}
	
	/**
	 * 
	 * @param e
	 * @param arrangementAndGrouping a string that specifies how the arguments should be rearranged
	 * and/or regrouped. Each argument is represented by a number, in order. Groups are specified 
	 * by parentheses. Example: "3 4 ( 1 5 ) ( 2 7 )"
	 * @return
	 */
	public static Expression argsToExpression(Expression e, String argOrderAndGrouping) {
		
		//TODO: make sure argOrderAndGrouping string is well-formed (this might best be done 
		//elsewhere - like right after the dialog box ok button is clicked)
		Expression result = null;
		if (e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			Operator op = oe.getOp();
			if (op.isCommutative() && op.isAssociative()) {
				String[] tokens = argOrderAndGrouping.split(" ");
				ArrayList<Expression> newArgs = new ArrayList<Expression>();
				for (int i=0; i < tokens.length; i++) {
					System.out.print("tokens: ");
					for (int j=0; j < tokens.length; j++) {
						System.out.print(tokens[j]+",");
					}
					String token = tokens[i];
					if (token.equals("(")) {
						//create new argOrderAndGrouping string s containing the
						//tokens between this "(" and the ")" that matches it
						//and use it to create a operatorExpression arg to this op
						String s = "";
						i++;
						int depth=0;
						while (depth >= 0) {
							switch (tokens[i]) {
								case "(": depth++; 
								          break;
								case ")": depth--; 
								          s = s.substring(0, s.length()-1);
								          break;
								default:  s = s + tokens[i] + " ";
							}
							if (depth >= 0) {
								i++;
							}
						}
						System.out.println(s);
						newArgs.add(argsToExpression(e,s));
					}
					else {
						int argIndex = Integer.parseInt(token) - 1;
						newArgs.add(oe.getArg(argIndex));
					}
				}
				return (new OperatorExpression(op,newArgs)).duplicate();
			}
		}
		
		//if you get this far, no rearrangement or regrouping was possible...
		System.out.println("unable to rearrange or regroup in this way");
		return result;
	}
	
	public static Expression factor(Expression e, OperatorExpression parent, OperatorExpression grandparent, VariableEnvironment variableEnvironment) {
		if (parent.getOp().equals(Operators.named("*")) && grandparent.getOp().equals(Operators.named("+"))) {
			System.out.println("in a product, inside a sum");
			// we must take (+ (* x y) (* x 3 z) (* x 1)) and turn it into
			//              (* x (+ y (* 3 z) 1)
			
			//TODO: can/should we tighten the user experience for the "1" above?
			//i.e., let the computer do the work for (+ (* x y) (* x 3 z) x)
			
			OperatorExpression p = (OperatorExpression) parent.duplicate();
			OperatorExpression gp = (OperatorExpression) grandparent.duplicate();
			
			for (int i=0; i < gp.getNumArgs(); i++) {
				if ((gp.getArg(i) instanceof OperatorExpression) && 
				    (((OperatorExpression) (gp.getArg(i))).getOp().equals(p.getOp()))) {
					OperatorExpression product = (OperatorExpression) (gp.getArg(i));
					product.getArgs().remove(e);
					if (product.getNumArgs() == 1) {
						Expression loneArg = product.getArg(0);
						gp.getArgs().remove(i);
						gp.getArgs().add(i, loneArg);
					}
				}
				else if (gp.getArg(i).equals(e)) {
					gp.getArgs().remove(i);
					gp.getArgs().add(Expression.parse("1",variableEnvironment));
				}
				else {
					return null; //not all terms of the grandparent expression were products 
				}
			}
			ArrayList<Expression> resultingProductFactors = new ArrayList<Expression>();
			resultingProductFactors.add(e);
			resultingProductFactors.add(gp);
			return (new OperatorExpression("*", resultingProductFactors)).duplicate();
		}
		
		if (parent.getOp().equals(Operators.named("+"))) { //it might be a lone term in a sum of products
			System.out.println("in a sum");
			OperatorExpression p = (OperatorExpression) parent.duplicate();
			System.out.println("parent: " + p.toString());
			for (int i=0; i < p.getNumArgs(); i++) {
				if ((p.getArg(i) instanceof OperatorExpression) && 
				    (((OperatorExpression) (p.getArg(i))).getOp().equals(Operators.named("*")))) {
					System.out.println("term encountered was a product: " + p.getArg(i).toString());
					OperatorExpression product = (OperatorExpression) (p.getArg(i));
					product.getArgs().remove(e);
					if (product.getNumArgs() == 1) {
						Expression loneArg = product.getArg(0);
						p.getArgs().remove(i);
						p.getArgs().add(i, loneArg);
					}
				}
				else if (p.getArg(i).equals(e)) {
					System.out.println("term encountered was expression e: " + p.getArg(i).toString());
					p.getArgs().remove(i);
					p.getArgs().add(i,Expression.parse("1",variableEnvironment));
				}
				else {
					System.out.println("term encountered was not a product or expression e: " + p.getArg(i).toString());
					return null; //not all terms of the parent expression were products or expression e
				}
			}
			System.out.println("parent after processing: " + p.toString());
			ArrayList<Expression> resultingProductFactors = new ArrayList<Expression>();
			resultingProductFactors.add(e);
			resultingProductFactors.add(p);
			return (new OperatorExpression("*", resultingProductFactors)).duplicate();
		}
		
		//if you get this far, there was a problem
		return null;
	}
	
	public static Expression commute(Expression e) {
		Expression result = null;
		Expression leftExpression = null;
		Expression rightExpression = null;
		
		System.out.println("expression trying to distribute: " + e);
		
		if (e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			Operator commutativeOp = oe.getOp();
			System.out.println("possible commutative operator: " + commutativeOp.toString());
			if (commutativeOp.isCommutative()) {
				System.out.println("confirmed, " + commutativeOp.toString() + " is commutative");
				leftExpression = oe.getArg(0);
				System.out.println("left expression : " + leftExpression);
				rightExpression = oe.getArg(1);
				System.out.println("right expression : " + rightExpression);
				ArrayList<Expression> pair = new ArrayList<Expression>();
				pair.add(rightExpression);
				pair.add(leftExpression);
				result = (new OperatorExpression(commutativeOp.toString(),pair)).duplicate();
				System.out.println("commuted expression : " + result);
				return result;
			}
		}
		
		//if you get this far, no distribution was possible...
		System.out.println("unable to commute");
		return null;
	}
	
	public static Expression distribute(Expression e) {
		Expression result = null;
		OperatorExpression opExpressionToDistributeInto = null;
		Expression expToDistribute = null;
		boolean canDistributeLeftExpression = false;
		boolean canDistributeRightExpression = false;
		
		System.out.println("expression trying to distribute: " + e);
		if (e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			Operator distributiveOp = oe.getOp();
			int numArgsForDistributiveOperator = oe.getNumArgs();
			System.out.println("distributive operator: " + distributiveOp.toString());
			if (numArgsForDistributiveOperator == NUM_ARGS_FOR_BINARY_OPERATOR) {
				
				// By default, try to distribute left factor into right expression -- unless this can't happen 
				// (i.e., right expression is an operator expression whose operator can't be distributed over,
				// or if the right expression isn't an operator expression)
				
				if (oe.getArg(1) instanceof OperatorExpression) {
					expToDistribute = oe.getArg(0);
					System.out.println("expression to distribute: " + expToDistribute);
					opExpressionToDistributeInto = (OperatorExpression) (oe.getArg(1));
					System.out.println("expression to distribute into: " + opExpressionToDistributeInto.toString());
					canDistributeLeftExpression = distributiveOp.distributesOver(opExpressionToDistributeInto.getOp());
				}
				else {
					expToDistribute = oe.getArg(1);
					System.out.println("expression to distribute: " + expToDistribute);
					opExpressionToDistributeInto = (OperatorExpression) (oe.getArg(0));
					System.out.println("expression to distribute into: " + opExpressionToDistributeInto.toString());
					canDistributeRightExpression = distributiveOp.distributesOver(opExpressionToDistributeInto.getOp());
				}
				
				// Do the distribution, if it is possible (again, left distribution by default -- unless there is a problem)
				
				if (canDistributeLeftExpression || canDistributeRightExpression) {
					int numArgsForExpressionToDistributeInto = opExpressionToDistributeInto.getNumArgs();
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					for (int i=0; i < numArgsForExpressionToDistributeInto; i++) {
						ArrayList<Expression> pair = new ArrayList<Expression>();
						if (canDistributeLeftExpression) {
							pair.add(expToDistribute);
							pair.add(opExpressionToDistributeInto.getArgs().get(i));
						}
						else { //canDistributeRightExpression
							pair.add(opExpressionToDistributeInto.getArgs().get(i));
							pair.add(expToDistribute);
						}
						newArgs.add(new OperatorExpression(distributiveOp.toString(),pair));
					}
					result = (new OperatorExpression(opExpressionToDistributeInto.getOp().toString(),newArgs)).duplicate();
					System.out.println("distributed over addition: " + result);
					return result;
				}
			}
		}
		
		//if you get this far, no distribution was possible...
		System.out.println("unable to distribute");
		return null; 
	}
	
	public static String getNumberedArgsLatex(Expression e, VariableEnvironment variableEnvironment) {
		StringBuilder sb = new StringBuilder("");
		if (e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			Operator op = oe.getOp();
			int numArgs = oe.getNumArgs();
			if (op.isCommutative()) {
				sb.append(op.toString());
				for (int i=0; i < numArgs; i++) {
					sb.append(" (underbrace ");
				    sb.append(oe.getArg(i) instanceof OperatorExpression ? "(": "");
				    sb.append(oe.getArg(i).toString());
				    sb.append(oe.getArg(i) instanceof OperatorExpression ? ")": "");
				    sb.append(" " + (i+1) + ")");
				}
				System.out.println("string with underbraces inserted: " + sb);
				Expression newExpression = Expression.parse(sb.toString(), variableEnvironment);
				System.out.println(newExpression.toLatex());
				return newExpression.toLatex();
			}
		}
		return "Whoops! Something went wrong.";
	}

}
