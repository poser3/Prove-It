package edu.emory.prove_it.expression;

import java.util.ArrayList;

public class Manipulator {
	
	public static Expression distribute(Expression e) {
		Expression result = null;
		boolean canDistribute = false;
		
		System.out.println("expression trying to distribute: " + e);
		if (e instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) e;
			Operator distributiveOp = oe.getOp();
			int numArgsForDistributiveOperator = oe.getNumArgs();
			System.out.println("outer-most operator: " + distributiveOp.toString());
			if (distributiveOp.toString().equals("*") && (numArgsForDistributiveOperator == 2)) {
				if (oe.getArg(1) instanceof OperatorExpression) {
					Expression expToDistribute = oe.getArg(0);
					System.out.println("expression to distribute: " + expToDistribute);
					OperatorExpression opExpressionToDistributeInto = (OperatorExpression) (oe.getArg(1));
					System.out.println("expression to distribute into: " + opExpressionToDistributeInto.toString());
					if (opExpressionToDistributeInto.getOp().toString().equals("+")) {
						canDistribute = true;
						int numArgsForExpressionToDistributeInto = opExpressionToDistributeInto.getNumArgs();
						ArrayList<Expression> newArgs = new ArrayList<Expression>();
						for (int i=0; i < numArgsForExpressionToDistributeInto; i++) {
							ArrayList<Expression> pair = new ArrayList<Expression>();
							pair.add(expToDistribute);
							pair.add(opExpressionToDistributeInto.getArgs().get(i));
							newArgs.add(new OperatorExpression("*",pair));
						}
						result = (new OperatorExpression("+",newArgs)).duplicate();
					}
				}
			}
		}
		
		if (canDistribute) {
			System.out.println("distributed over addition: " + result);
			return result;
		}
		else {
			System.out.println("unable to distribute");
			return null;
		}
	}

}
