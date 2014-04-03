package edu.emory.prove_it.sketch_canvas;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class PPoints extends ArrayList<PPoint> {

	public void update() {
		for (PPoint p : this)
			p.update();
	}
	
	public PPoints getSelected() {
		PPoints selectedPoints = new PPoints();
		for (PPoint p : this)
			if (p.isSelected())
				selectedPoints.add(p);
		return selectedPoints;
	}

}
