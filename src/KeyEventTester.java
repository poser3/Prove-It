import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import acm.program.*;
import acm.graphics.*;

public class KeyEventTester extends GraphicsProgram {

	public final int LEFT = 37;
	public final int UP = 38;
	public final int RIGHT = 39;
	public final int DOWN = 40;
	
	private GOval dot_;
	
	public void run() {
		
		//draw a dot on the screen
		dot_ = new GOval(100,100,20,20);
		dot_.setFilled(true);
		dot_.setFillColor(Color.RED);
		this.add(dot_);
		
		//set up the key listener
		this.getGCanvas().setFocusable(true);
		this.getGCanvas().requestFocus();
		this.getGCanvas().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println("typed" + e.getKeyChar() + ":" + e.getKeyCode());
			}

			@Override
			//move the dot up, down, left, or right as appropriate...
			public void keyPressed(KeyEvent e) {
				System.out.println("pressed" + e.getKeyChar() + ":" + e.getKeyCode());
				switch (e.getKeyCode()) {
				case UP :    dot_.move(0,-1); break;  
				case DOWN :  dot_.move(0,1); break;
				case LEFT :  dot_.move(-1, 0); break;
				case RIGHT : dot_.move(1, 0); break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});
	}
}

