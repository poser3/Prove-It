package edu.emory.prove_it.sketch_canvas;
/**
 * MadeWith2Points provides methods for dealing with geometric objects constructed from two points
 * (segments, lines, rays, and circles).
 * @author Paul Oser, Lee Vian
 *
 */
public interface MadeWith2Points {
	
	public PPoint get1stPoint();
	public PPoint get2ndPoint();
	public void set1stPoint(PPoint p1);
	public void set2ndPoint(PPoint p2);
	
	public String getLabel();
	
}
