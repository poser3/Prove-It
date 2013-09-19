import java.awt.Color;
import acm.graphics.GLine;
import acm.graphics.GPoint;

@SuppressWarnings("serial")
public class PLine extends GLine implements Drawable, Selectable, MadeWith2Points {

	public static final double POINT_DIAMETER = 10;
	public static final double EPSILON = 0.001;
	public static final int FREE_LINE = 1;
	public static final int SEGMENT_BISECTOR = 2;
	
	private final String label_;
	private PPoint p1_;
	private PPoint p2_;
	private double xMin_;
	private double xMax_;
	private double yMin_;
	private double yMax_;
	private boolean selected_;
	
	public GPoint[] getEdgePoints() {
		
		//declare temporary vars
		double x=0;
		double y=0;
		double t1=0;
		double t2=0;
		double t3=0;
		double t4=0;
		boolean isVertical = false;
		boolean isHorizontal = false;
		
		//Provided they are calculatable, use a parametric version of the line through p1 and p2 to find
		//the four t values for where this line crosses the lines at xMin, xMax, yMin, yMax.  When these are not 
		//calculatable, the line must be either vertical or horizontal -- set the associated booleans accordingly
		
		if (Math.abs(p2_.getPointX() - p1_.getPointX()) > EPSILON) {
		     t1 = (xMin_ - p1_.getPointX())/(p2_.getPointX() - p1_.getPointX());
		     t2 = (xMax_ - p1_.getPointX())/(p2_.getPointX() - p1_.getPointX());
		}
		else {
			isVertical = true;
		}
		
		if (Math.abs(p2_.getPointY() - p1_.getPointY()) > EPSILON) {
		     t3 = (yMin_ - p1_.getPointY())/(p2_.getPointY() - p1_.getPointY());
		     t4 = (yMax_ - p1_.getPointY())/(p2_.getPointY() - p1_.getPointY());
		}
		else {
			isHorizontal = true;
		}
		
		if (isVertical) {
			x = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t1;
			y = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t1;
		}
		else if (isHorizontal) {
			x = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t3;
			y = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t3;
		}
		else {
			//find center point of viewing rectangle
			double cx = (xMin_ + xMax_) / 2.0;
			double cy = (yMin_ + yMax_) / 2.0;
			
			//compute both possible starting points
			double x1 = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t1;
			double y1 = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t1;
			double x2 = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t3;
			double y2 = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t3;
			
			//make (x,y) the one closer to the center point of the viewing rectangle (use taxicab metric)
			if (Math.abs(cx-x1) + Math.abs(cy-y1) <= Math.abs(cx-x2) + Math.abs(cy-y2)) {
				x = x1;
				y = y1;
			}
			else {
				x = x2;
				y = y2;
			}
		}
		
		//store this first bounding point (x,y) in a GPoint 
		GPoint e1 = new GPoint(x,y); 
				
		if (isVertical) {
			x = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t2;
			y = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t2;
		}
		else if (isHorizontal) {
			x = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t4;
			y = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t4;
		}
		else {
			//find center point of viewing rectangle
			double cx = (xMin_ + xMax_) / 2.0;
			double cy = (yMin_ + yMax_) / 2.0;
			
			//compute both possible starting points
			double x1 = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t2;
			double y1 = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t2;
			double x2 = p1_.getPointX() + (p2_.getPointX() - p1_.getPointX())*t4;
			double y2 = p1_.getPointY() + (p2_.getPointY() - p1_.getPointY())*t4;
			
			//make (x,y) the one closer to the center point of the viewing rectangle (use taxicab metric)
			if (Math.abs(cx-x1) + Math.abs(cy-y1) <= Math.abs(cx-x2) + Math.abs(cy-y2)) {
				x = x1;
				y = y1;
			}
			else {
				x = x2;
				y = y2;
			}
		}
		
		//store this first bounding point (x,y) in a GPoint 
		GPoint e2 = new GPoint(x,y); 
				
		//create a GPoint array to hold p1 and p2
		GPoint[] points = new GPoint[2];
		points[0] = e1;
		points[1] = e2;
		
		//return the array
		return points;
	}
	
	public PLine(PPoint p1, PPoint p2, String label, Double xMin, Double xMax, Double yMin, Double yMax) {
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
		//TODO: If a line would be vertical or horizontal, instead it temporarily turns invisible.
		
		GPoint[] edgePoints = this.getEdgePoints();
		this.setStartPoint(edgePoints[0].getX(),edgePoints[0].getY());
		this.setEndPoint(edgePoints[1].getX(),edgePoints[1].getY());
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
		double ax = p1_.getPointX();
		double ay = p1_.getPointY();
		double bx = p2_.getPointX();
		double by = p2_.getPointY();
		double normalLength = Math.hypot(bx - ax, by - ay);
		return Math.abs((x-ax)*(by-ay) - (y-ay)*(bx-ax)) / normalLength;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PLine) {
			return getLabel().equals(((PLine) o).getLabel());
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s from (%f.1, %f.1) to (%f.1, %f.1)",
				getLabel(),
				p1_.getPointX(), p1_.getPointY(),
				p2_.getPointX(), p2_.getPointY());
	}
	
	
}
