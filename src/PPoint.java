import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GOval;

@SuppressWarnings("serial")
public class PPoint extends GCompound implements Drawable, Selectable {

	public static final double POINT_DIAMETER = 10;
	public static final double EPSILON = 1;
	public static final double SUPER_TINY = 0.0001;
	
	public static final byte FREE_POINT = 0;
	public static final byte MIDPOINT = 1;
	public static final byte INTERSECTION_OF_LINES = 2;
	public static final byte LEFT_INTERSECTION_OF_CIRCLES = 3;
	public static final byte RIGHT_INTERSECTION_OF_CIRCLES = 4;
	public static final byte LEFT_INTERSECTION_OF_CIRCLE_AND_LINE = 5;
	public static final byte RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE = 6;
	public static final byte INTERSECTON_OF_RAY_AND_LINE = 7;
	public static final byte INTERSECTON_OF_RAYS = 8;
	public static final byte LEFT_INTERSECTION_OF_RAY_AND_CIRCLE = 9;
	public static final byte RIGHT_INTERSECTION_OF_RAY_AND_CIRCLE = 10;
	public static final byte INTERSECTION_OF_SEGMENT_AND_LINE = 11;
	public static final byte INTERSECTON_OF_SEGMENTS = 12;
	public static final byte LEFT_INTERSECTION_OF_SEGMENT_AND_CIRCLE = 13;
	public static final byte RIGHT_INTERSECTION_OF_SEGMENT_AND_CIRCLE = 14;
	public static final byte INTERSECTION_OF_SEGMENT_AND_RAY = 15;

	public static double distance(PPoint p1, PPoint p2) {
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}

	private GOval dot_;
	private byte constructedAs_;
	private ArrayList<Drawable> parents_;
	private final String label_;
	private boolean selected_;
	private GLabel gLabel_;
	private boolean exists_;
	
	public PPoint(double x, double y, String label) {
		
		this.setLocation(x,y);
		dot_ = new GOval(-POINT_DIAMETER / 2.0, -POINT_DIAMETER / 2.0, POINT_DIAMETER, POINT_DIAMETER);
		dot_.setFilled(true);
		dot_.setFillColor(Color.BLACK);
		this.add(dot_);
		
		label_ = label;
		gLabel_ = new GLabel(label_, 10, -10);
		this.add(gLabel_);
		
		constructedAs_ = FREE_POINT;
		exists_ = true;
	}
	
	public PPoint(byte constructedAs, Collection<? extends Drawable> parents, String label) {
		dot_ = new GOval(-POINT_DIAMETER / 2.0, -POINT_DIAMETER / 2.0, POINT_DIAMETER, POINT_DIAMETER);
		dot_.setFilled(true);
		dot_.setFillColor(Color.BLACK);
		this.add(dot_);
		parents_ = new Drawables(parents);
		label_ = label;
		gLabel_ = new GLabel(label_, 10, -10);
		this.add(gLabel_);
		constructedAs_ = constructedAs;
		exists_ = true;
		update();
	}
	
	@Override
	public void move(double dx, double dy) {
		
		switch (constructedAs_) {
		
		case FREE_POINT : 
						  //move the point itself
						  super.move(dx, dy);
						  break;
						  
		case MIDPOINT :   
						  //move the point itself
		  				  super.move(dx, dy);
		  				  //now move the parents
		  				  for (int i=0; i < parents_.size(); i++) {
		  					  parents_.get(i).move(dx, dy);
		  				  }
		  				  break;
		}
	}
	
	public boolean exists() {
		return exists_;
	}
	
	public void setExists(boolean exists) {
		exists_ = exists;
	}
	
	public void update() {
		
		//scratch variables to improve readability of calculations...
		double x,y;
		PLine line, line1, line2, pL1, pL2;
		PRay ray, ray1, ray2;
		PCircle circle, circle1, circle2;
		PPoint intersectionPt, p1, p2;
		PSegment segment, segment1, segment2;
		int whichPt;
		
		switch (constructedAs_) {
		
		case FREE_POINT :
			//setLocation(x_, y_);
			break;
		                 
		case MIDPOINT :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				p1 = (PPoint) parents_.get(0);
	        	p2 = (PPoint) parents_.get(1);
	        	x = (p1.getX() + p2.getX()) / 2.0;
	        	y = (p1.getY() + p2.getY()) / 2.0;
	        	setLocation(x, y);
	        	this.setExists(true);
        	}
        	else {
        		this.setExists(false);  //one or more of the parent points didn't exist
        	}
        	break;
		                  
		case INTERSECTION_OF_LINES : 
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				line1 = (PLine) parents_.get(0);
				line2 = (PLine) parents_.get(1);
				intersectionPt = Intersection.ofTwoLines(line1, line2);
				if (intersectionPt != null) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //lines did not intersect (they were parallel)
				}
			}
			else {
				this.setExists(false);  //one or more of the parent lines didn't exist
			}
			break;
			
		case INTERSECTON_OF_RAY_AND_LINE :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int rayIndex = ((parents_.get(0) instanceof PRay) ? 0 : 1);
				int lineIndex = ((parents_.get(0) instanceof PLine) ? 0 : 1);
				ray = (PRay) parents_.get(rayIndex);
				pL1 = (PLine) parents_.get(lineIndex);
				pL2 = new PLine(ray.get1stPoint(),ray.get2ndPoint(),"");
				intersectionPt = Intersection.ofTwoLines(pL1, pL2);
				if ((intersectionPt != null) && Intersection.pointOnRay(ray,intersectionPt)) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false); //either intersection of related lines didn't exist or
										   //intersection of related lines is not on the ray
				}
			}
			else {
				this.setExists(false);  //either the parent line or ray (or both) didn't exist
			}
			break;
			
		case INTERSECTION_OF_SEGMENT_AND_LINE :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int segmentIndex = ((parents_.get(0) instanceof PSegment) ? 0 : 1);
				int lineIndex = ((parents_.get(0) instanceof PLine) ? 0 : 1);
				segment = (PSegment) parents_.get(segmentIndex);
				line1 = (PLine) parents_.get(lineIndex);
				line2 = new PLine(segment.get1stPoint(),segment.get2ndPoint(),"");
				intersectionPt = Intersection.ofTwoLines(line1, line2);
				if ((intersectionPt != null) && Intersection.pointOnSegment(segment,intersectionPt)) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false); //either intersection of related lines didn't exist or
										   //intersection of related lines is not on the segment
				}
			}
			else {
				this.setExists(false);  //either the parent line or segment (or both) didn't exist
			}
			break;
			
		case INTERSECTION_OF_SEGMENT_AND_RAY :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int segmentIndex = ((parents_.get(0) instanceof PSegment) ? 0 : 1);
				int rayIndex = ((parents_.get(0) instanceof PRay) ? 0 : 1);
				segment = (PSegment) parents_.get(segmentIndex);
				ray = (PRay) parents_.get(rayIndex);
				line1 = new PLine(ray.get1stPoint(),ray.get2ndPoint(),"");
				line2 = new PLine(segment.get1stPoint(),segment.get2ndPoint(),"");
				intersectionPt = Intersection.ofTwoLines(line1, line2);
				if ((intersectionPt != null) && Intersection.pointOnSegment(segment,intersectionPt) && Intersection.pointOnRay(ray, intersectionPt)) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false); //either intersection of related lines didn't exist or
										   //intersection of related lines is not on the segment or not on the ray
				}
			}
			else {
				this.setExists(false);  //either the parent line or segment (or both) didn't exist
			}
			break;
			
		
		case INTERSECTON_OF_SEGMENTS :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				segment1 = (PSegment) parents_.get(0);
				segment2 = (PSegment) parents_.get(1);
				line1 = new PLine(segment1.get1stPoint(),segment1.get2ndPoint(),"");
				line2 = new PLine(segment2.get1stPoint(),segment2.get2ndPoint(),"");
				intersectionPt = Intersection.ofTwoLines(line1, line2);
				if ((intersectionPt != null) && Intersection.pointOnSegment(segment1,intersectionPt) && 
												Intersection.pointOnSegment(segment2, intersectionPt)) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //either intersection of related lines didn't exist or
					   				        //intersection of related lines is not on one (or both) of the segments
				}
			}
			else {
				this.setExists(false);  //one or more of the parent segments didn't exist
			}
			break;
			
		case INTERSECTON_OF_RAYS :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				ray1 = (PRay) parents_.get(0);
				ray2 = (PRay) parents_.get(1);
				pL1 = new PLine(ray1.get1stPoint(),ray1.get2ndPoint(),"");
				pL2 = new PLine(ray2.get1stPoint(),ray2.get2ndPoint(),"");
				intersectionPt = Intersection.ofTwoLines(pL1, pL2);
				if ((intersectionPt != null) && Intersection.pointOnRay(ray1,intersectionPt) && Intersection.pointOnRay(ray2, intersectionPt)) {
					setLocation(intersectionPt.getX(), intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //either intersection of related lines didn't exist or
					   				        //intersection of related lines is not on one (or both) of the rays
				}
			}
			else {
				this.setExists(false);  //one or more of the parent rays didn't exist
			}
			break;
		  				  
		case LEFT_INTERSECTION_OF_CIRCLE_AND_LINE :
		case RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int circleIndex = ((parents_.get(0) instanceof PCircle) ? 0 : 1);
				int lineIndex = ((parents_.get(1) instanceof PLine) ? 1 : 0);
				circle = (PCircle) parents_.get(circleIndex);
				line = (PLine) parents_.get(lineIndex);
				whichPt = (constructedAs_ == RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE ? Intersection.RIGHT : Intersection.LEFT);
				intersectionPt = Intersection.ofLineAndCircle(line, circle, whichPt);
				if (intersectionPt != null) {
					setLocation(intersectionPt.getX(),intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //circle and line fail to intersect
				}
			}
			else {
				this.setExists(false); //one or more of the circles fails to exist
			}
			break;
			
		case LEFT_INTERSECTION_OF_RAY_AND_CIRCLE :
		case RIGHT_INTERSECTION_OF_RAY_AND_CIRCLE :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int circleIndex = ((parents_.get(0) instanceof PCircle) ? 0 : 1);
				int rayIndex = ((parents_.get(1) instanceof PRay) ? 1 : 0);
				circle = (PCircle) parents_.get(circleIndex);
				ray = (PRay) parents_.get(rayIndex);
				line = new PLine(ray.get1stPoint(),ray.get2ndPoint(),"");
				whichPt = (constructedAs_ == RIGHT_INTERSECTION_OF_RAY_AND_CIRCLE ? Intersection.RIGHT : Intersection.LEFT);
				intersectionPt = Intersection.ofLineAndCircle(line, circle, whichPt);
				if ((intersectionPt != null) && Intersection.pointOnRay(ray,intersectionPt)) {
					setLocation(intersectionPt.getX(),intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //circle and ray fail to intersect
				}
			}
			else {
				this.setExists(false); //one or more of the circles fails to exist
			}
			break;
			
		case LEFT_INTERSECTION_OF_SEGMENT_AND_CIRCLE :
		case RIGHT_INTERSECTION_OF_SEGMENT_AND_CIRCLE :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				int circleIndex = ((parents_.get(0) instanceof PCircle) ? 0 : 1);
				int segmentIndex = ((parents_.get(1) instanceof PSegment) ? 1 : 0);
				circle = (PCircle) parents_.get(circleIndex);
				segment = (PSegment) parents_.get(segmentIndex);
				line = new PLine(segment.get1stPoint(),segment.get2ndPoint(),"");
				whichPt = (constructedAs_ == RIGHT_INTERSECTION_OF_SEGMENT_AND_CIRCLE ? Intersection.RIGHT : Intersection.LEFT);
				intersectionPt = Intersection.ofLineAndCircle(line, circle, whichPt);
				if ((intersectionPt != null) && Intersection.pointOnSegment(segment,intersectionPt)) {
					setLocation(intersectionPt.getX(),intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false);  //circle and segment fail to intersect
				}
			}
			else {
				this.setExists(false); //either the segment or the circle involved fails to exist
			}
			break;
		  				  
		case RIGHT_INTERSECTION_OF_CIRCLES :
		case LEFT_INTERSECTION_OF_CIRCLES :
			if (parents_.get(0).exists() && parents_.get(1).exists()) {
				circle1 = (PCircle) parents_.get(0);
				circle2 = (PCircle) parents_.get(1);
				whichPt = (constructedAs_ == RIGHT_INTERSECTION_OF_CIRCLES ? Intersection.RIGHT : Intersection.LEFT);
				intersectionPt = Intersection.ofTwoCircles(circle1, circle2, whichPt);
				if (intersectionPt != null) {
					setLocation(intersectionPt.getX(),intersectionPt.getY());
					this.setExists(true);
				}
				else {
					this.setExists(false); // circles fail to intersect
				}
			}
			else {
				this.setExists(false); //one or more of the circles fails to exist
			} 
			break;
		}
		
		//set visibility (and maybe other things?) as appropriate when 
		//point in question exists, and when it doesn't...
		if (this.exists()) {
			this.dot_.setVisible(this.exists());
			this.gLabel_.setVisible(this.exists());
		}
		else {
			this.dot_.setVisible(false);
			this.gLabel_.setVisible(false);
		}
	}
	
	public void setSelected(boolean selected) {
		selected_ = selected;
		dot_.setFillColor(selected ? Color.MAGENTA : Color.BLACK);
	}
	
	public boolean isSelected() {
		return selected_;
	}
	
	public String getLabel() {
		return label_;
	}
	
	public double distanceTo(double x, double y) {
		return Math.sqrt( (x-this.getX())*(x-this.getX()) +
				          (y-this.getY())*(y-this.getY())   );
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PPoint) {
			return label_.equals(((PPoint) o).getLabel());
		}
		else {
			return false;
		}
	}
	
	public double getDistanceTo(double x, double y) {
		return Math.sqrt((x-this.getX())*(x-this.getX()) + (y-this.getY())*(y-this.getY()));
	}
	
	@Override
	public String toString() {
		return "Point " + this.getLabel() + " at (" + getX() + ", " + getY() + ")";
	}
	
}

