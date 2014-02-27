import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GLine;
import acm.graphics.GPoint;

@SuppressWarnings("serial")
public class PRay extends GCompound implements Drawable, Selectable, MadeWith2Points {

	public static final double POINT_DIAMETER = 10;
	public static final double EPSILON = 0.001;
	public static final int FREE_RAY = 1;
	public static final int ANGLE_BISECTOR = 2;
	
	public static ViewingRectangle viewingRectangle;
	
	private static final double EDGE_OFFSET = 20;
	
	private GLine gLine_;
	private final String label_;
	private FancyLabel fancyLabel_;
	private PPoint p1_;
	private PPoint p2_;
	private Drawables parents_;
	private Drawables dependents_;
	private boolean selected_;
	private boolean exists_;
	
	public GPoint getEdgePoint() {
		
		return viewingRectangle.findBoundingRectIntersection(p1_, p2_);
	}
	
	public PRay(PPoint p1, PPoint p2, String label) {
		gLine_ = new GLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		this.add(gLine_);
		p1_ = p1;
		p2_ = p2;
		parents_ = new Drawables();
		parents_.add(p1_);
		parents_.add(p2_);
		dependents_ = new Drawables();
		label_ = label;
		fancyLabel_ = new FancyLabel(label_);
		this.add(fancyLabel_);
		setSelected(false);
		exists_ = true;
		update();
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
		GPoint edgePoint = this.getEdgePoint();
		
		gLine_.setStartPoint(p1_.getX(), p1_.getY());
		if (edgePoint != null) {
			gLine_.setEndPoint(edgePoint.getX(),edgePoint.getY());
		}
		else {
			gLine_.setEndPoint(p1_.getX(), p1_.getY());
		}
		
		double rxMin = viewingRectangle.getXMin() + EDGE_OFFSET;
		double rxMax = viewingRectangle.getXMax() - EDGE_OFFSET;
		double ryMin = viewingRectangle.getYMin() + EDGE_OFFSET;
		double ryMax = viewingRectangle.getYMax() - EDGE_OFFSET;
		ViewingRectangle labelRectangle = new ViewingRectangle(rxMin, rxMax, ryMin, ryMax);
		GPoint labelLocationPt = labelRectangle.findBoundingRectIntersection(p1_, p2_);
		if (labelLocationPt != null) {
			fancyLabel_.setLocation(labelLocationPt);
		}
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
		return String.format("(ray %s)", getLabel());
	}
	
	@Override
	public double distanceTo(double x, double y) {
		//Suppose we want distance from X to segment AB
		//Find angle BAX using law of cosines.
		//Then, 
		//if BAX > 90 use distance to A
		//otherwise use distance to line AB
		
		double ax = p1_.getX();
		double ay = p1_.getY();
		double bx = p2_.getX();
		double by = p2_.getY();
		
		double sideABsquared = (ax-bx)*(ax-bx)+(ay-by)*(ay-by);
		double sideAXsquared = (ax-x)*(ax-x)+(ay-y)*(ay-y);
		double sideBXsquared = (bx-x)*(bx-x)+(by-y)*(by-y);
		
		boolean angleBAXisObtuse = (sideABsquared + sideAXsquared - sideBXsquared < 0);
		
		if (angleBAXisObtuse) {
			return Math.sqrt(sideAXsquared); //distance to A
		}
		else {
			double normalLength = Math.hypot(bx - ax, by - ay);
			return Math.abs((x-ax)*(by-ay) - (y-ay)*(bx-ax)) / normalLength; //distance to line AB
		}
				
	}
	
	@Override
	public boolean equals(Object o) {
		//TODO: equals here or equals for PPoint is not working right.  Fix this.
		if (o instanceof PRay)
			return getLabel().equals(((PRay) o).getLabel());
		else
			return false;
	}
	
	@Override
	public String toString() {
		return String.format("%s from (%f.1, %f.1) towards (%f.1, %f.1)",
				getLabel(),
				p1_.getX(), p1_.getY(),
				p2_.getX(), p2_.getY());
	}
	
}
