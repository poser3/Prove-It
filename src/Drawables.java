import java.util.ArrayList;
import java.util.Collection;

/**
 * A Drawables object holds several drawables.
 * @author Paul Oser, Lee Vian
 *
 */
@SuppressWarnings("serial")
public class Drawables extends ArrayList<Drawable> {
	
	/**
	 * Create an empty list of drawables.
	 */
	public Drawables() {
		super();
	}
	/**
	 * Create a list of drawables from the provided collection.
	 * @param c a Collection of drawables.
	 */
	public Drawables(Collection<? extends Drawable> c) {
		super(c);
	}
	
	/**
	 * Update each drawable in the list.
	 */
	public void update() {
		for (Drawable d : this)
			d.update();
	}
	
	public void addInOrder(Drawable drawable1, Drawable drawable2) {
		this.add(drawable1);
		this.add(drawable2);
	}
}

