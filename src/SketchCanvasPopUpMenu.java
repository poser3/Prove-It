import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class SketchCanvasPopUpMenu extends JPopupMenu {
	
	MainWindow mainWindow_;
	JMenuItem pairItem_;
	MouseEvent mouseEvent_;
	String selectedDrawableExpressionString_;
	
	public SketchCanvasPopUpMenu(MainWindow mainWindow, MouseEvent mouseEvent) {
	    	
		mainWindow_ = mainWindow;
		mouseEvent_ = mouseEvent;

		//TODO: if we need it, here is how we can get the drawable that was right-clicked to produce the pop-up menu
		Drawable drawableClicked = mainWindow_.getSketchCanvas().getDrawableAt(mouseEvent_.getX(), mouseEvent_.getY());
		
		int indexOfSelectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedIndex();
    	
		pairItem_ = new JMenuItem("Pair selected object with selected theorem variable");
		pairItem_.setEnabled((mainWindow_.getSketchCanvas().getSelectedDrawables().size() == 1)  &&
	                         (indexOfSelectedPairing != -1));
		pairItem_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int indexOfSelectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedIndex();
				System.out.println("index of selected variable in theorem panel = " + indexOfSelectedPairing);
				System.out.println("selectedDrawableString = " + mainWindow_.getSketchCanvas().getSelectedDrawables().get(0).expression());
				mainWindow_.getTheoremPanel().getPairings().get(indexOfSelectedPairing).pair(Expression.parse(mainWindow_.getSketchCanvas().getSelectedDrawables().get(0).expression()));
				mainWindow_.getTheoremPanel().update();
			}});
    	add(pairItem_);
	    	
	}
}
