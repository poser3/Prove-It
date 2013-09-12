package edu.emory.oxford.proofpad.sketch;

public interface Selectable {
	
	public final double EPSILON = 10;
	
	public boolean isSelected();
	public void setSelected(boolean selected);
	
}
