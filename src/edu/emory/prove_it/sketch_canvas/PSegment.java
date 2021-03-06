package edu.emory.prove_it.sketch_canvas;
import java.awt.Color;

import edu.emory.prove_it.Selectable;
import edu.emory.prove_it.expression.Type;
import acm.graphics.GLine;

@SuppressWarnings("serial")
public class PSegment extends GLine implements Drawable, Selectable, MadeWith2Points {

	public static final double POINT_DIAMETER = 10;
	
	private final String label_;
	private PPoint p1_;
	private PPoint p2_;
	private Drawables parents_;
	private Drawables dependents_;
	private boolean selected_;
	private boolean exists_;
	
	
	public PSegment(PPoint p1, PPoint p2, String label) {
		super(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		p1_ = p1;
		p2_ = p2;
		parents_ = new Drawables();
		parents_.add(p1_);
		parents_.add(p2_);
		dependents_ = new Drawables();
		label_ = label;
		setSelected(false);
		exists_ = true;
	}
	
	@Override
	public Drawables getParents() {
		return parents_;
	}
	
	@Override
	public Drawables getDependents() {
		return dependents_;
	}
	
	@Override
	public boolean exists() {
		return exists_;
	}
	
	@Override
	public void setExists(boolean exists) {
		exists_ = exists;
	}
	
	@Override
	public void update() {
		setStartPoint(p1_.getX(), p1_.getY());
		setEndPoint(p2_.getX(), p2_.getY());
	}
	
	@Override
	public void setSelected(boolean selected) {
		selected_ = selected;
		setColor(selected ? Color.MAGENTA : Color.BLACK);
	}
	
	@Override
	public boolean isSelected() {
		return selected_;
	}
	
	@Override
	public PPoint get1stPoint() {
		return p1_;
	}
	
	@Override
	public PPoint get2ndPoint() {
		return p2_;
	}
	
	@Override
	public void set1stPoint(PPoint p1) {
		p1_ = p1;
	}
	
	@Override
	public void set2ndPoint(PPoint p2) {
		p2_ = p2;
	}
	
	@Override
	public String getLabel() {
		return label_;
	}
	
	@Override
	public String expression() {
		return String.format("(segment %s %s)", get1stPoint().expression(), get2ndPoint().expression());
	}
	
	@Override
	public Type getType() {
		return Type.SEGMENT;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PSegment) {
			return (get1stPoint().equals(((PSegment) o).get1stPoint())
						&& get2ndPoint().equals(((PSegment) o).get2ndPoint()))
					|| (get1stPoint().equals(((PSegment) o).get2ndPoint())
							&& get2ndPoint().equals(((PSegment) o).get1stPoint()));
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.getLabel() + " from " + 
	           "(" + p1_.getX() + ", " + p1_.getY() + ")" +
	           " to " + 
	    	   "(" + p2_.getX() + ", " + p2_.getY() + ")";
	}

	@Override
	public double distanceTo(double x, double y) {
		//Suppose we want distance from X to segment AB
		//Find angles ABX and BAX using law of cosines.
		//Then, 
		//if ABX > 90 use distance to B
		//if BAX > 90 use distance to A
		//otherwise use distance to line AB
		
		double ax = p1_.getX();
		double ay = p1_.getY();
		double bx = p2_.getX();
		double by = p2_.getY();
		
		double sideABsquared = (ax-bx)*(ax-bx)+(ay-by)*(ay-by);
		double sideAXsquared = (ax-x)*(ax-x)+(ay-y)*(ay-y);
		double sideBXsquared = (bx-x)*(bx-x)+(by-y)*(by-y);
		
		boolean angleABXisObtuse = (sideABsquared + sideBXsquared - sideAXsquared < 0);
		boolean angleBAXisObtuse = (sideABsquared + sideAXsquared - sideBXsquared < 0);
		
		if (angleABXisObtuse) {
			return Math.sqrt(sideBXsquared); //distance to B
		}
		else if (angleBAXisObtuse) {
			return Math.sqrt(sideAXsquared); //distance to A
		}
		else {
			double normalLength = Math.hypot(bx - ax, by - ay);
			return Math.abs((x-ax)*(by-ay) - (y-ay)*(bx-ax)) / normalLength; //distance to line AB
		}
		
	}
	
}
