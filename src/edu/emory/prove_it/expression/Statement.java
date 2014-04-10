package edu.emory.prove_it.expression;
import java.util.ArrayList;
import java.util.HashMap;

import edu.emory.prove_it.sketch_canvas.Drawable;
import edu.emory.prove_it.sketch_canvas.Drawables;


public class Statement implements Comparable<Statement> {
	
	private final Expression expression;
	private final ArrayList<Statement> logicDependencies;
	private final Drawables geometryDependencies;
	private boolean hidden = false;
	private boolean valid = false;
	
	public Statement(final Expression expression) {
		this.expression = expression;
		this.logicDependencies = new ArrayList<Statement>();
		this.geometryDependencies = new Drawables();
	}
	public Statement(final String expression, final VariableEnvironment environment) {
		this.expression = Expression.parse(expression, environment);
		this.logicDependencies = new ArrayList<Statement>();
		this.geometryDependencies = new Drawables();
	}
	
	public Statement(final Expression expression, final ArrayList<Statement> logicDependencies, final Drawables geometryDependencies) {
		this.expression = expression;
		
		this.logicDependencies = (logicDependencies == null) ? new ArrayList<Statement>() : logicDependencies;
		this.geometryDependencies = (geometryDependencies == null) ? new Drawables() : geometryDependencies;
	}
	public Statement(final String expression, final VariableEnvironment environment, final ArrayList<Statement> logicDependencies, final Drawables geometryDependencies) {
		this.expression = Expression.parse(expression, environment);
		
		this.logicDependencies = (logicDependencies == null) ? new ArrayList<Statement>() : logicDependencies;
		this.geometryDependencies = (geometryDependencies == null) ? new Drawables() : geometryDependencies;
	}
	
	public Expression getExpression() {
		return expression;
	}
	@Override
	public String toString() {
		return expression.toString();
	}
	public String toLatex() {
		return expression.toLatex();
	}
	
	public boolean equals(Statement s) {
		return expression.equals(s.getExpression());
	}
	@Override
	public int compareTo(Statement s) {
		return expression.compareTo(s.getExpression());
	}
	
	public Statement substitute(Expression quid, Expression quo) {
		return new Statement(expression.substitute(quid, quo), logicDependencies, geometryDependencies);
	}
	
	public Statement substituteSelectedIntoDuplicate(Expression quo) {
		return new Statement(expression.substituteSelected(quo).duplicate(), logicDependencies, geometryDependencies);
	}
	
	public Statement substitute(HashMap<String, String> map) {
		return new Statement(expression.substitute(map));
	}
	
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isValid() {
		// The instance variable represents whether this statement is valid on its own merits.
		if (! valid)
			return false;
		// Also check all the dependencies: an invalid dependency invalidates this statement.
		else {
			// Logic dependencies
			for (Statement s : logicDependencies)
				if (! s.isValid())
					return false;
			for (Drawable d : geometryDependencies)
				if (! d.exists())
					return false;
			// If this.valid and all of the dependencies are also valid, return true.
			return true;
		}
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public ArrayList<Statement> logicParents() {
		return logicDependencies;
	}
	public Drawables geometryParents() {
		return geometryDependencies;
	}
	
}