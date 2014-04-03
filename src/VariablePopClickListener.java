import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class VariablePopClickListener extends MouseAdapter {
	
	MainWindow mainWindow_;
	
	public VariablePopClickListener(MainWindow mainWindow) {
		mainWindow_ = mainWindow;
	}
	
	public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
    	
    	// Note to self: you can check to see if the mouse was right-clicked here with: 
    	//
    	// 	  SwingUtilities.isRightMouseButton(e)
    	//
    	// but the popup menu only shows up when right-clicked, so this probably doesn't matter

		VariablePopUpMenu menu = new VariablePopUpMenu(mainWindow_, e);
		menu.show(e.getComponent(), e.getX(), e.getY());
  
    }
}
