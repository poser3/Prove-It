package edu.emory.prove_it.sketch_canvas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.emory.prove_it.MainWindow;


public class SketchCanvasPopClickListener extends MouseAdapter {
	
	MainWindow mainWindow_;
	
	public SketchCanvasPopClickListener(MainWindow mainWindow) {
		mainWindow_ = mainWindow;
	}
	
	@Override
	public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    @Override
	public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){

		SketchCanvasPopUpMenu menu = new SketchCanvasPopUpMenu(mainWindow_, e);
		menu.show(e.getComponent(), e.getX(), e.getY());
  
    }
}