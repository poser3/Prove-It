package edu.emory.prove_it.theorem;

import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.VariableExpression;

public class Pairing {
	
	private VariableExpression variableExpression_;
	private Expression pairedExpression_;
	private boolean isPaired_;
	
	public Pairing(VariableExpression variableExpression, Expression pairedExpression) {
		variableExpression_ = variableExpression;
		if (pairedExpression == null) {
			isPaired_ = false;
		}
		else {
			pairedExpression_ = pairedExpression;
			isPaired_ = true;
		}
	}
	
	public VariableExpression getVariableExpression() {
		return variableExpression_;
	}
	
	public Expression getPairedExpression() {
		return pairedExpression_;
	}
	
	public boolean isPaired() {
		return isPaired_;
	}
	
	public void pair(Expression e) {
		pairedExpression_ = e;
		isPaired_ = (e != null);
	}
	
	@Override
	public String toString() {
		return (isPaired_ ? variableExpression_ + " <- " + pairedExpression_ : variableExpression_ + " unpaired");
	}
}
