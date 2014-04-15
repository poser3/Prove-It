package edu.emory.prove_it.expression;

import java.util.ArrayList;

public class Manipulator {
	
	public static int NUM_ARGS_FOR_BINARY_OPERATOR = 2;
	
	public static Expression dropParensOnSum(Expression e) {
		Expression result = null;
		OperatorExpression oe;
		System.out.println("Entered dropParensOnSum()");
		if (e instanceof OperatorExpression) {
			oe = (OperatorExpression) e;
			Operator sumOperator = oe.getOp();
			System.out.println("operator found: " + sumOperator);
			if (sumOperator.equals(new Operator("+"))) {
				System.out.println("operator found was a sum operator");
				
				//now check if each operator is a sum
			}
		}
		
		//if you get this far, the parens could not be dropped
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

}
