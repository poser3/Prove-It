import java.util.ArrayList;
import acm.graphics.GPoint;

public class AnalyticGeometryUtils {

	public static GPoint findBoundingRectIntersection(double p1x, double p1y, 
							  double p2x, double p2y, 
							  double xMin, double xMax, double yMin, double yMax) {
		
		ArrayList<GPoint> points = new ArrayList<GPoint>();
		
		double t,x,y;
		
		//System.out.println("----");
		if (Math.abs(p2y-p1y) > 0.0001) { //not horizontal, so safe to find intersections with y = yMin and y = yMax
			t = (yMin - p1y)/(p2y - p1y);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((x > xMin) && (x < xMax)) {  //if it hits the line y = yMin, between xMin and xMax
				//System.out.printf("added intersection with y=yMin at (%.2f,%.2f)\n",x,y);
				points.add(new GPoint(x,y));
			}
			
			t = (yMax - p1y)/(p2y - p1y);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((x > xMin) && (x < xMax)) {  //if it hits the line y = yMax, between xMin and xMax 
				//System.out.printf("added intersection with y=yMax at (%.2f,%.2f)\n",x,y);
				points.add(new GPoint(x,y));
			}
		}
		
		if (Math.abs(p2x-p1x) > 0.0001) { //not vertical, so safe to find intersections with x = xMin and x = xMax
			t = (xMin - p1x)/(p2x - p1x);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((y > yMin) && (y < yMax)) { //if it hits the line x = xMin, between yMin and yMax
				//System.out.printf("added intersection with x=xMin at (%.2f,%.2f)\n",x,y);
				points.add(new GPoint(x,y));
			}
			
			t = (xMax - p1x)/(p2x - p1x);
			x = p1x + (p2x - p1x)*t;
			y = p1y + (p2y - p1y)*t;
			if ((y > yMin) && (y < yMax)) { //if it hits the line x = xMax, between yMin and yMax
				//System.out.printf("added intersection with x=xMax at (%.2f,%.2f)\n",x,y);
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

