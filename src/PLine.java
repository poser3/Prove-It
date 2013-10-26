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
		
		GPoint[] edgePts = new GPoint[2];
		edgePts[0] = AnalyticGeometryUtils.findBoundingRectIntersection(p1_.getPointX(), p1_.getPointY(), 
																		    p2_.getPointX(), p2_.getPointY(), 
																		    xMin_, xMax_, yMin_, yMax_);
		edgePts[1] = AnalyticGeometryUtils.findBoundingRectIntersection(p2_.getPointX(), p2_.getPointY(), 
																		    p1_.getPointX(), p1_.getPointY(), 
																		    xMin_, xMax_, yMin_, yMax_);
		return edgePts;
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
		GPoint[] edgePoints = this.getEdgePoints();
		
		if ((edgePoints[0] != null) && (edgePoints[1] != null)) {
			this.setStartPoint(edgePoints[0].getX(),edgePoints[0].getY());
			this.setEndPoint(edgePoints[1].getX(),edgePoints[1].getY());
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
