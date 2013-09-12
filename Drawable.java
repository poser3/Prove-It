package edu.emory.oxford.proofpad.sketch;

/**
 * Drawable provides common methods for objects which can be shown on a SketchCanvas.
 * @author Paul Oser, Lee Vian
 *
 */
public interface Drawable {
	
	void update();
	void move(double dx, double dy);
	double distanceTo(double x, double y);
	public void setSelected(boolean select);
	public boolean isSelected();
	public String getLabel();

}
