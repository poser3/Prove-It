package edu.emory.prove_it.util;
import java.util.ArrayList;

import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.OperatorExpression;


public class Shifter {

	private Expression topNode_;
	
	public Shifter(Expression topNode) {
		topNode_ = topNode;
	}
	
	public int countNodesRecursive(Expression e) {
		Expression node = e;
		
		if (!(node instanceof OperatorExpression)) {
			return 1;
		}
		
		else {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			int count = 1;
			for (int i=0; i < args.size(); i++) {
				count += countNodesRecursive(args.get(i));
			}
			return count;
		}
	}
	
	public void shiftSelectionBackward() {
		int count = countNodesRecursive(topNode_);
		for (int i=0; i < count-1; i++) {
			shiftSelectionForward();
		}
	}
	
	public void shiftSelectionForward() {
		shiftSelectionForwardRecursive(topNode_);
	}
	
	public void shiftSelectionForwardRecursive(Expression e) {
		Expression node = e;
		
		if (node instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			
			if (node.isSelected()) {
				shiftSelectionDeepestLeft();
			}
			
			else {
				//cycle through each argument to this operator expression...
				for (int i=0; i < args.size(); i++) {
					
					//if the argument being considered is selected, and their is another argument to its right
					//move the selection right and then as deep as possible within that argument to the right
					if ((args.get(i).isSelected()) && (i < args.size() - 1)) {
						args.get(i).setSelected(false);
						args.get(i+1).setSelected(true);
						shiftSelectionDeepestLeft();
						break; // i.e., don't look for another selected expression to shift
					}
					
					//if the argument being considered is selected, and there is no argument to its right
					//move the selection up (i.e., select expression node which has the considered argument)
					else if ((args.get(i).isSelected()) && (i == args.size() - 1)) {
						args.get(i).setSelected(false);
						node.setSelected(true);
						break; 
					}
					
					//if the argument being considered was not selected, try to find a selection deeper that can be addressed
					//note: this exploration deeper happens simultaneously with exploration of arguments farther to the right
					//so if multiple things are selected somehow, multiple things should get pushed forwards -- but this
					//shouldn't happen, as only one subexpression should be selectable at a time.
					else {
						shiftSelectionForwardRecursive(args.get(i));
					}
				}
			}
		}		
	}
	
	public void shiftSelectionRight() {
		shiftSelectionRightRecursive(topNode_);
	}
	
	public void shiftSelectionRightRecursive(Expression e) {
		Expression node = e;
		
		if (node instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			
			//step through arguments backwards so you don't push an expression
			//to the right more than once..
			for (int i=args.size() - 1; i >= 0; i--) { 
				if (args.get(i).isSelected() && (i < args.size() - 1)) {
					args.get(i).setSelected(false);
					args.get(i+1).setSelected(true);
				}
				
				shiftSelectionRightRecursive(args.get(i));
			}
		}
	}
	
	public void shiftSelectionLeft() {
		shiftSelectionLeftRecursive(topNode_);
	}
	
	public void shiftSelectionLeftRecursive(Expression e) {
		Expression node = e;
		
		if (node instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			
			for (int i=0; i < args.size(); i++) { 
				if (args.get(i).isSelected() && (i > 0)) {
					args.get(i).setSelected(false);
					args.get(i-1).setSelected(true);
				}
				
				shiftSelectionLeftRecursive(args.get(i));
			}
		}
	}
	
	public void shiftSelectionUp() {
		shiftSelectionUpRecursive(topNode_);
	}
	
	public void shiftSelectionUpRecursive(Expression e) {
		Expression node = e;
		
		if (node instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			
			for (int i=0; i < args.size(); i++) { 
				if (args.get(i).isSelected()) {
					args.get(i).setSelected(false);
					node.setSelected(true);
				}
				
				shiftSelectionUpRecursive(args.get(i));
			}
		}
	}
	
	public void shiftSelectionDown() {
		shiftSelectionDownRecursive(topNode_);
	}
	
	public void shiftSelectionDeepestLeft() {
		for (int i=0; i < 100; i++) {
			shiftSelectionDown();
		}
	}
	
	public void shiftSelectionDownRecursive(Expression e) {
		Expression node = e;
		
		if (node instanceof OperatorExpression) {
			OperatorExpression oe = (OperatorExpression) node;
			ArrayList<Expression> args = oe.getArgs();
			
			if (oe.isSelected()) {
				oe.setSelected(false);
				args.get(0).setSelected(true);
			}
			
			else {
				for (int i=0; i < args.size(); i++) { 
					Expression arg = args.get(i);
					if (arg.isSelected() && (arg instanceof OperatorExpression)) {
						OperatorExpression aoe = (OperatorExpression) arg;
						ArrayList<Expression> subArgs = aoe.getArgs();
						args.get(i).setSelected(false);
						subArgs.get(0).setSelected(true);
					}
					else {
						shiftSelectionDownRecursive(arg);
					}
				}
			}
		}
	}
}
