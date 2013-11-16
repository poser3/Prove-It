import java.util.ArrayList;


public class Statement {
	
	private final Expression expression;
	private ArrayList<Statement> logicDependencies = new ArrayList<Statement>();
	private Drawables geometryDependencies = new Drawables();
	private boolean hidden = false;
	private boolean valid = false;
	
	public Statement(final Expression expression) {
		this.expression = expression;
	}
	public Statement(final String expression) {
		this.expression = Expression.parse(expression);
	}
	
	public Statement(final Expression expression, final ArrayList<Statement> logicDependencies) {
		this.expression = expression;
		this.logicDependencies = logicDependencies;
	}
	public Statement(final String expression, final ArrayList<Statement> logicDependencies) {
		this.expression = Expression.parse(expression);
		this.logicDependencies = logicDependencies;
	}
	public Statement(final Expression expression, final Drawables geometryDependencies) {
		this.expression = expression;
		this.geometryDependencies = geometryDependencies;
	}
	public Statement(final String expression, final Drawables geometryDependencies) {
		this.expression = Expression.parse(expression);
		this.geometryDependencies = geometryDependencies;
	}
	public Statement(final Expression expression, final ArrayList<Statement> logicDependencies, final Drawables geometryDependencies) {
		this.expression = expression;
		this.logicDependencies = logicDependencies;
		this.geometryDependencies = geometryDependencies;
	}
	public Statement(final String expression, final ArrayList<Statement> logicDependencies, final Drawables geometryDependencies) {
		this.expression = Expression.parse(expression);
		this.logicDependencies = logicDependencies;
		this.geometryDependencies = geometryDependencies;
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
			// TODO Uncomment when this file and the rays-and-intersections branch are brought together.
			/*
			for (Drawable d : geometryDependencies)
				if (! d.exists())
					return false;
			 */
			// If this.valid and all of the dependencies are also valid, return true.
			return true;
		}
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
}
