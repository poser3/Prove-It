import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


@SuppressWarnings("serial")
public class SketchCanvasPopUpMenu extends JPopupMenu {
	
	MainWindow mainWindow_;
	JMenuItem pairItem_;
	MouseEvent mouseEvent_;
	String selectedDrawableExpressionString_;
	
	public SketchCanvasPopUpMenu(MainWindow mainWindow, MouseEvent mouseEvent) {
	    	
		mainWindow_ = mainWindow;
		mouseEvent_ = mouseEvent;

		//TODO: if we need it, here is how we can get the drawable that was right-clicked to produce the pop-up menu
		//Drawable drawableClicked = mainWindow_.getSketchCanvas().getDrawableAt(mouseEvent_.getX(), mouseEvent_.getY());
		
		final Pairing selectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedValue();
		final Type pairingType = selectedPairing == null ? null : selectedPairing.getVariableExpression().getType();
		final Drawable selectedDrawable = mainWindow_.getSketchCanvas().getSelectedDrawables().size() == 1 ?
				mainWindow_.getSketchCanvas().getSelectedDrawables().get(0) : null;
		final Type drawableType = selectedDrawable == null ? null : selectedDrawable.getType();
    	
		pairItem_ = new JMenuItem("Pair selected object with selected theorem variable");
		pairItem_.setEnabled((selectedPairing != null) && (selectedDrawable != null)
				&& (pairingType == drawableType));
		pairItem_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression drawableExpression = Expression.parse(selectedDrawable.expression(),
						mainWindow_.getVariableEnvironment());
				
				selectedPairing.pair(drawableExpression);
				mainWindow_.getTheoremPanel().update();
			}});
    	add(pairItem_);
	    	
	}
}
