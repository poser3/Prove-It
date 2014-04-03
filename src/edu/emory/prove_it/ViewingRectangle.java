package edu.emory.prove_it;
import java.util.ArrayList;

import edu.emory.prove_it.sketch_canvas.PPoint;
import acm.graphics.GPoint;

public class ViewingRectangle {
	
	private double xMin_;
	private double xMax_;
	private double yMin_;
	private double yMax_;
	
	public ViewingRectangle(double xMin, double xMax, double yMin, double yMax) {
		xMin_ = xMin;
		yMin_ = yMin;
		xMax_ = xMax;
		yMax_ = yMax;
	}
	
	public double getXMin() {
		return xMin_;
	}
	
	public double getXMax() {
		return xMax_;
	}
	
	public double getYMin() {
		return yMin_;
	}
	
	public double getYMax() {
		return yMax_;
	}
	
	/* returns distance to a point p1 that is inside the rectangle, -1 returned otherwise */
	/* TODO: get the method to return -minDistance if point outside rectangle */
	public double distanceTo(GPoint p) {
		double minDistance = -1;
		if (p.getX() - xMin_ > 0) {
			minDistance = p.getX() - xMin_;
		}
		if (xMax_ - p.getX() > 0) {
			minDistance = Math.min(minDistance, xMax_ - p.getX());
		}
		if (p.getY() - yMin_ > 0) {
			minDistance = Math.min(minDistance, p.getY() - yMin_);
		}
		if (yMax_ - p.getY() > 0) {
			minDistance = Math.min(minDistance, yMax_ - p.getY());
		}
		return minDistance;
	}

	/**
	 * Returns the point of intersection of a ray starting at (p1x, p1y) with 2nd point (p2x, p2y) and the 
	 * viewing rectangle.  Currently both points must be INSIDE the rectangle for this to work correctly.
	 * @param p1x
	 * @param p1y
	 * @param p2x
	 * @param p2y
	 * @return
	 */
	public GPoint findBoundingRectIntersection(PPoint p1, PPoint p2) {
		
		ArrayList<GPoint> points = new ArrayList<GPoint>();
		
		double t,x,y;
		double p1x = p1.getX();
		double p1y = p1.getY();
		double p2x = p2.getX();
		double p2y = p2.getY();
		
		//System.out.println("----");
		if (Math.abs(p2y-p1y) > 0.0001) { //not horizontal, so safe to find intersections with y = yMin and y = yMax
			t = (yMin_ - p1y)/(p2y - p1y);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((x > xMin_) && (x < xMax_)) {  //if it hits the line y = yMin, between xMin and xMax
				points.add(new GPoint(x,y));
			}
			
			t = (yMax_ - p1y)/(p2y - p1y);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((x > xMin_) && (x < xMax_)) {  //if it hits the line y = yMax, between xMin and xMax 
				points.add(new GPoint(x,y));
			}
		}
		
		if (Math.abs(p2x-p1x) > 0.0001) { //not vertical, so safe to find intersections with x = xMin and x = xMax
			t = (xMin_ - p1x)/(p2x - p1x);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((y > yMin_) && (y < yMax_)) { //if it hits the line x = xMin, between yMin and yMax
				points.add(new GPoint(x,y));
			}
			
			t = (xMax_ - p1x)/(p2x - p1x);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((y > yMin_) && (y < yMax_)) { //if it hits the line x = xMax, between yMin and yMax
				points.add(new GPoint(x,y));
			}
		}
		
		//there are most often 2 intersections, A0 and A1.  When this is the case
		//check to see if P2 is closer to A0 than P1.  If it is, A0 is the edge
		//point you want.  Otherwise, A1 is the edge point you want
		switch (points.size()) {
		case 2:
			double a0x = points.get(0).getX();
			double a0y = points.get(0).getY();
			double squaredDistP1toA0 = ((p1x - a0x)*(p1x - a0x)+
										(p1y - a0y)*(p1y - a0y));
			double squaredDistP2toA0 = ((p2x - a0x)*(p2x - a0x)+
										(p2y - a0y)*(p2y - a0y));

			if (squaredDistP2toA0 < squaredDistP1toA0) {
				return points.get(0);
			}
			else {
				return points.get(1);
			}
		case 1:
			return points.get(0);
		default:
			return null;
		}
		
	}
}
