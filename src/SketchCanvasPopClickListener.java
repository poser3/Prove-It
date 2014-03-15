
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.SwingUtilities;


public class SketchCanvasPopClickListener extends MouseAdapter {
	
	MainWindow mainWindow_;
	
	public SketchCanvasPopClickListener(MainWindow mainWindow) {
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

		SketchCanvasPopUpMenu menu = new SketchCanvasPopUpMenu(mainWindow_, e);
		menu.show(e.getComponent(), e.getX(), e.getY());
  
    }
}