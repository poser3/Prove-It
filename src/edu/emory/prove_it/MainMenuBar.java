package edu.emory.prove_it;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import edu.emory.prove_it.theorem.Theorem;
import edu.emory.prove_it.theorem.TheoremMenuActionListener;


@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar{
	
	//SETUP THE MENU SYSTEM...
	//JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;
	MainWindow mainWindow_;
	
	public MainMenuBar(MainWindow mainWindow) {

		super();
			
		mainWindow_ = mainWindow;
		
		menu = new JMenu("File");
		
		menuItem = new JMenuItem("Load Statements...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow_.loadStatements();	
			}});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Reload Theorems");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow_.reloadTheorems();	
			}});
		menu.add(menuItem);
		
		this.add(menu);
		
		
		//////////////////////////////////////////////////////////
		
		//Build the Select menu in the menu bar.
		menu = new JMenu("Select");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription(
				"Select or deselect items...");
		
		menuItem = new JMenuItem("Deselect All");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow_.deselectAll();
			}});
		
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Unhide All Drawables");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow_.getSketchCanvas().unhideAllDrawables();
			}});
		
		menu.add(menuItem);
		
		this.add(menu);
		
		//////////////////////////////////////////////////////////
		
		//Build the theorems menu in the menu bar.
		menu = new JMenu("Theorems");
		menu.setMnemonic(KeyEvent.VK_T);
		menu.getAccessibleContext().setAccessibleDescription(
		        "Select a theorem to apply...");
		
		Theorem.loadTheorems();
		for (Theorem t : Theorem.theorems) {
			menuItem = new JMenuItem(t.toString());
			menuItem.addActionListener(new TheoremMenuActionListener(mainWindow_.getTheoremPanel(), t));
			menu.add(menuItem);
		}
		//this.add(menu); //<----- uncomment this if you wish to return the theorems menu to the main menu bar (this is probably not desired)
		
		///////////////////////////////////////////////////////////
		
		//Build an example menu.
		menu = new JMenu("Example Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "Showcase of different kinds of menu items");
		//menuBar.add(menu);
		//this.add(menu);           //<---uncomment this line to make the example menu reappear
		
		//a group of JMenuItems
		menuItem = new JMenuItem("A text-only menu item",
		                         KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "This doesn't really do anything");
		menu.add(menuItem);

		menuItem = new JMenuItem("Both text and icon",
		                         new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);
		
		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Another one");
		rbMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		//a group of check box menu items
		menu.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("Another one");
		cbMenuItem.setMnemonic(KeyEvent.VK_H);
		menu.add(cbMenuItem);

		//a submenu
		menu.addSeparator();
		submenu = new JMenu("A submenu");
		submenu.setMnemonic(KeyEvent.VK_S);

		menuItem = new JMenuItem("An item in the submenu");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		submenu.add(menuItem);

		menuItem = new JMenuItem("Another item");
		submenu.add(menuItem);
		menu.add(submenu);
		
		//////////////////////////////////////////////////////////
		
		//Build another menu in the menu bar.
		menu = new JMenu("Another Menu");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
		        "This menu does nothing");
		//menuBar.add(menu);
		//this.add(menu);      //<----- uncomment this line to make the 2nd example menu re-appear.
		

	}

}
