import java.awt.Color;
import acm.graphics.GLine;
import acm.graphics.GPoint;

@SuppressWarnings("serial")
public class PRay extends GLine implements Drawable, Selectable, MadeWith2Points {

	public static final double POINT_DIAMETER = 10;
	public static final double EPSILON = 0.001;
	public static final int FREE_RAY = 1;
	public static final int ANGLE_BISECTOR = 2;
	
	private final String label_;
	private PPoint p1_;
	private PPoint p2_;
	private double xMin_;
	private double xMax_;
	private double yMin_;
	private double yMax_;
	private boolean selected_;
	
	public GPoint getEdgePoint() {
		
		return AnalyticGeometryUtils.findBoundingRectIntersection(p1_.getPointX(), p1_.getPointY(), 
																  p2_.getPointX(), p2_.getPointY(), 
																  xMin_, xMax_, yMin_, yMax_);
	}
	
	public PRay(PPoint p1, PPoint p2, String label, Double xMin, Double xMax, Double yMin, Double yMax) {
		super(p1.getPointX(), p1.getPointY(), p2.getPointX(), p2.getPointY());
		p1_ = p1;
		p2_ = p2;
		xMin_ = xMin;
		xMax_ = xMax;
		yMin_ = yMin;
		yMax_ = yMax;
		label_ = label;
		setSelected(false);
		update();
	}
	
	public void update() {
		GPoint edgePoint = getEdgePoint();
		
		setStartPoint(p1_.getPointX(), p1_.getPointY());
		if (edgePoint != null) {
			setEndPoint(edgePoint.getX(),edgePoint.getY());
		}
		else {
			setEndPoint(p1_.getPointX(), p1_.getPointY());
		}
	}
	
	public void setSelected(boolean selected) {
		selected_ = selected;
		setColor(selected ? Color.MAGENTA : Color.BLACK);
	}
	
	public boolean isSelected() {
		return selected_;
	}
	
	public PPoint get1stPoint() {
		return p1_;
	}
	
	public PPoint get2ndPoint() {
		return p2_;
	}
	
	public void set1stPoint(PPoint p1) {
		p1_ = p1;
	}
	
	public void set2ndPoint(PPoint p2) {
		p2_ = p2;
	}
	
	public String getLabel() {
		return label_;
	}
	
	public double distanceTo(double x, double y) {
		//Suppose we want distance from X to segment AB
		//Find angle BAX using law of cosines.
		//Then, 
		//if BAX > 90 use distance to A
		//otherwise use distance to line AB
		
		double ax = p1_.getPointX();
		double ay = p1_.getPointY();
		double bx = p2_.getPointX();
		double by = p2_.getPointY();
		
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
	
	public boolean equals(Object o) {
		//TODO: equals here or equals for PPoint is not working right.  Fix this.
		if (o instanceof PRay)
			return getLabel().equals(((PRay) o).getLabel());
		else
			return false;
	}
	
	public String toString() {
		return String.format("%s from (%f.1, %f.1) towards (%f.1, %f.1)",
				getLabel(),
				p1_.getPointX(), p1_.getPointY(),
				p2_.getPointX(), p2_.getPointY());
	}
	
}
