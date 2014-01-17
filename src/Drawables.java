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
	
	public static void registerWithParents(Drawable drawable) {
		System.out.println("registering " + drawable + " with parents...");
		for (int i = 0; i < drawable.getParents().size(); i++) {
			drawable.getParents().get(i).getDependents().add(drawable);
			System.out.println("Added " + drawable + " as dependent to " + drawable.getParents().get(i));
		}
	}
	
	public static boolean allDependentsExist(Drawables dependents) {
		boolean dependentsExist = true;
		for (int i=0; i < dependents.size(); i++) {
			//WARNING: THE BELOW SETS UP A DOUBLE-RECURSION THAT COULD BE TIME-COSTLY IF TOO
			//MANY PARENTS ARE INVOLVED -- THIS COULD BE DONE BETTER...
			if ((! dependents.get(i).exists()) || (! allDependentsExist(dependents.get(i).getDependents()))) {
				dependentsExist = false;
			}
		}
		return dependentsExist;
	}
	
}
