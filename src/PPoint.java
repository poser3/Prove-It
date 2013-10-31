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
	
	public static final byte FREE_POINT = 0;
	public static final byte MIDPOINT = 1;
	public static final byte INTERSECTION_OF_LINES = 2;
	public static final byte LEFT_INTERSECTION_OF_CIRCLES = 3;
	public static final byte RIGHT_INTERSECTION_OF_CIRCLES = 4;
	public static final byte LEFT_INTERSECTION_OF_CIRCLE_AND_LINE = 5;
	public static final byte RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE = 6;

	public static double distance(PPoint p1, PPoint p2) {
		double x1 = p1.getPointX();
		double y1 = p1.getPointY();
		double x2 = p2.getPointX();
		double y2 = p2.getPointY();
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}

	private GOval dot_;
	private double x_;
	private double y_;
	private byte constructedAs_;
	private ArrayList<Drawable> parents_;
	private final String label_;
	private boolean selected_;
	private GLabel gLabel_;
	
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
		
		x_ = x;
		y_ = y;
	}
	
	public PPoint(byte constructedAs, Collection<? extends Drawable> parents, String label) {
		dot_ = new GOval(0, 0, POINT_DIAMETER, POINT_DIAMETER);
		dot_.setFilled(true);
		dot_.setFillColor(Color.BLACK);
		parents_ = new Drawables(parents);
		label_ = label;
		constructedAs_ = constructedAs;
		update();
	}
	
	@Override
	public void move(double dx, double dy) {
		
		switch (constructedAs_) {
		
		case FREE_POINT : //move the point itself
						  x_ += dx;
						  y_ += dy;
						  super.move(dx, dy);
						  break;
						  
		case MIDPOINT :   //move the point itself
						  x_ += dx;
		  				  y_ += dy;
		  				  super.move(dx, dy);
		  				  //now move the parents
		  				  for (int i=0; i < parents_.size(); i++) {
		  					  parents_.get(i).move(dx, dy);
		  				  }
		  				  break;
		}
	}
	
	public void update() {
		
		//scratch variables to improve readability of calculations...
		double x0,y0,x1,y1,x2,y2,xj,yj,a1,a2,b1,b2,t1,rj,r1,r2,d,h,xm,xn,yn,a,b,f,g,t,sign;
		PLine pL1, pL2;
		PCircle c1, c2;
		
		switch (constructedAs_) {
		
		case FREE_POINT :
			setLocation(x_, y_);
			break;
		                 
		case MIDPOINT :
			PPoint p1 = (PPoint) parents_.get(0);
        	PPoint p2 = (PPoint) parents_.get(1);
        	x_ = (p1.getPointX() + p2.getPointX()) / 2.0;
        	y_ = (p1.getPointY() + p2.getPointY()) / 2.0;
        	setLocation(x_, y_);
        	break;
		                  
		case INTERSECTION_OF_LINES : 
			pL1 = (PLine) parents_.get(0);
			pL2 = (PLine) parents_.get(1);
			x1 = pL1.get1stPoint().getPointX();
			y1 = pL1.get1stPoint().getPointY();
			x2 = pL2.get1stPoint().getPointX();
			y2 = pL2.get1stPoint().getPointY();
			a1 = pL1.get2ndPoint().getPointX() - x1;
			a2 = pL2.get2ndPoint().getPointX() - x2;
			b1 = pL1.get2ndPoint().getPointY() - y1;
			b2 = pL2.get2ndPoint().getPointY() - y2;
			t1 = (b2*(x1-x2) + a2*(y2-y1))/(a2*b1-a1*b2);
			x_ = x1 + a1*t1;
			y_ = y1 + b1*t1;
			setLocation(x_, y_);
			break;
		  				  
		case LEFT_INTERSECTION_OF_CIRCLE_AND_LINE :
		case RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE :
			c1 = (PCircle) parents_.get(0);
			pL2 = (PLine) parents_.get(1);
			rj = c1.getRadius();
			xj = c1.getCenter().getPointX();
			yj = c1.getCenter().getPointY();
			x0 = pL2.get1stPoint().getPointX();
			y0 = pL2.get1stPoint().getPointY();
			x1 = pL2.get2ndPoint().getPointX();
			y1 = pL2.get2ndPoint().getPointY();
			f = x1-x0;
			g = y1-y0;
			sign = (constructedAs_ == RIGHT_INTERSECTION_OF_CIRCLE_AND_LINE ? 1 : -1);
			t = (f*(xj - x0) + g*(yj-y0) + sign * Math.sqrt(rj*rj * (f*f + g*g) - 
					(f*(y0 - yj) - g*(x0-xj))*(f*(y0 - yj) - g*(x0-xj))))/(f*f+g*g);
			x_ = x0 + f*t;
			y_ = y0 + g*t;
			setLocation(x_, y_);
			break;
		  				  
		case RIGHT_INTERSECTION_OF_CIRCLES :
		case LEFT_INTERSECTION_OF_CIRCLES :
			c1 = (PCircle) parents_.get(0);
			c2 = (PCircle) parents_.get(1);
			r1 = c1.getRadius();
			r2 = c2.getRadius();
			x1 = c1.getCenter().getPointX();
			y1 = c1.getCenter().getPointY();
			x2 = c2.getCenter().getPointX();
			y2 = c2.getCenter().getPointY();
			p1 = c1.getCenter();
			p2 = c2.getCenter();
			
			d = distance(p1, p2);          //d is distance between centers
			              
			xm = (d*d + r1*r1 - r2*r2)/(2*d);
				//xm is the distance from p1 to the closest point
				//on the segment connecting their centers to the intersection
			              
			h = Math.sqrt((4*d*d*r1*r1 - Math.pow(d*d-r2*r2+r1*r1,2))/(4*d*d));
				//h is the distance from the pt of intersection to the line
				//connectingthe centers
			              
			xn = (y2-y1) / d;	//(xn,yn) is a unit vector normal to the
			yn = -(x2-x1) / d;	//segment connecting the centers
			              
			a = x1 + (x2-x1)*xm/d;
			b = y1 + (y2-y1)*xm/d;
			              
			sign = (constructedAs_ == RIGHT_INTERSECTION_OF_CIRCLES ? 1 : -1);
			x_ = a + sign*h*xn;
			y_ = b + sign*h*yn;
			setLocation(x_, y_);
			break;
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
		return Math.sqrt( (x-this.getPointX())*(x-this.getPointX()) +
				          (y-this.getPointY())*(y-this.getPointY())   );
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
	
	public double getPointX() {
		return this.getX();
		//return x_;
	}
	
	public double getPointY() {
		return this.getY();
		//return y_;
	}
	
	public double getDistanceTo(double x, double y) {
		return Math.sqrt((x-x_)*(x-x_) + (y-y_)*(y-y_));
	}
	
	@Override
	public String toString() {
		return "Point " + this.getLabel() + " at (" + getPointX() + ", " + getPointY() + ")";
	}
	
}

