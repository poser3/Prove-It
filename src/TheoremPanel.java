import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import acm.gui.TableLayout;


@SuppressWarnings("serial")
public class TheoremPanel extends JPanel {
	
		public final int PAIRINGS_SCROLLPANE_WIDTH = 200;
		public final int PAIRINGS_SCROLLPANE_HEIGHT = 150;
		public final int HYPOTHESES_SCROLLPANE_WIDTH = 200;
		public final int HYPOTHESES_SCROLLPANE_HEIGHT = 50;
		public final int CONCLUSIONS_SCROLLPANE_WIDTH = 200;
		public final int CONCLUSIONS_SCROLLPANE_HEIGHT = 50;
		public final int VERT_SPACE_ABOVE_THEOREM_APPLY_BUTTON = 30;
		
		MainWindow mainWindow_;
		final JButton chooseTheoremButton;
		final JButton pairButton;
		final JButton applyTheoremButton;
		private DefaultListModel<Pairing> pairings_;
	    private JList<Pairing> pairingsList_;
	    private final DefaultListModel<Statement> hypotheses = new DefaultListModel<Statement>();
		private final DefaultListModel<Statement> conclusions = new DefaultListModel<Statement>();
	    private final JList<Statement> hypothesesList = new JList<Statement>(hypotheses);
		private final JList<Statement> conclusionsList = new JList<Statement>(conclusions);
		private Theorem currentTheorem_;
		private JLabel currentTheoremJLabel_;
		
		@Override
		public String toString() {
			return "Theorem panel has current theorem of : " + currentTheorem_;
		}
		
		public void update() {
			pairingsList_.update(pairingsList_.getGraphics());
		}
		
		public void setCurrentTheorem(Theorem theorem) {
			//set current theorem instance variable
			currentTheorem_ = theorem;
			
			//update theorem JLabel
			currentTheoremJLabel_.setText(currentTheorem_.toString());
			
			//update hypotheses and conclusions...
			hypotheses.clear();
			conclusions.clear();
			for (Statement s : currentTheorem_.hypotheses)
				hypotheses.addElement(s);
			for (Statement s : currentTheorem_.conclusions)
				conclusions.addElement(s);
			
			//start with blank pairings...
			pairings_.clear();
			for (Expression v : currentTheorem_.variables) {
				if (v instanceof VariableExpression) {
					Pairing pairing = new Pairing((VariableExpression) v, null);
					pairings_.addElement(pairing);
				}
				else {
					System.out.println("There was a problem: expected a variable expression");
				}
			}
			pairButton.setEnabled(true);
		}
		
		public TheoremPanel(MainWindow mainWindow) {
			mainWindow_ = mainWindow;
			
			currentTheoremJLabel_ = new JLabel("Select a theorem from the menu...");
			
			hypothesesList.setCellRenderer(new StatementListCellRenderer());
			conclusionsList.setCellRenderer(new StatementListCellRenderer());
			
			pairings_ = new DefaultListModel<Pairing>();
			pairingsList_ = new JList<Pairing>(pairings_);
			
			pairingsList_.setCellRenderer(new PairingListCellRenderer());
			JScrollPane pairingsScrollPane = new JScrollPane(pairingsList_);
			pairingsList_.setFocusable(true);
			
			pairingsScrollPane.setVerticalScrollBar(pairingsScrollPane.createVerticalScrollBar());
			pairingsScrollPane.setPreferredSize(new Dimension(PAIRINGS_SCROLLPANE_WIDTH,PAIRINGS_SCROLLPANE_HEIGHT));
			
			chooseTheoremButton = new JButton("Choose Theorem");
			chooseTheoremButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TheoremChooserDialog chooser = new TheoremChooserDialog(mainWindow_);
					chooser.setVisible(true);
					Theorem theorem = chooser.getSelected();
					if (theorem != null) {
						pairings_.clear();
						for (Expression v : theorem.variables) {
							if (v instanceof VariableExpression) {
								Pairing pairing = new Pairing((VariableExpression) v, null);
								pairings_.addElement(pairing);
							}
							else {
								System.out.println("There was a problem: expected a variable expression");
							}
						}
					}
				}});
			
			pairButton = new JButton("Pair Selected");
			pairButton.setEnabled(false);
			
			applyTheoremButton = new JButton("Apply Theorem");
			applyTheoremButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					HashMap<String, String> substitutions = new HashMap<String, String>();
					
					TheoremPanel.this.mainWindow_.setInstructionsText(""); //for DEBUGGING
					
					for (int i=0; i < pairings_.getSize(); i++) {
						String replace = pairings_.get(i).getVariableExpression().toString();
						String with = pairings_.get(i).getPairedExpression().toString();
						substitutions.put(replace, with);
						
						//for DEBUGGING...
						String status = "applying theorem" + "\n" +
				                "replace : " + replace + "\n" +
						        "with : " + with + "\n";
						TheoremPanel.this.mainWindow_.appendInstructionsText(status);
					}
					
					ArrayList<Statement> parents = new ArrayList<Statement>();
					
					for (Statement h : currentTheorem_.hypotheses) {
						Expression postReplacement = h.getExpression().substitute(substitutions);
						boolean found = false;
						for (Object s : mainWindow_.getStatementPanel().getStatements().toArray()) {
							Expression expr = ((Statement) s).getExpression();
							System.out.println(expr + " =? " + postReplacement + " : " + expr.equals(postReplacement));
							if (expr.equals(postReplacement)) {
								found = true;
								parents.add((Statement) s);
								break;
							}
						}
						
						if (! found) {
							mainWindow_.appendInstructionsText("Hypothesis \"" + 
									postReplacement.toString() +
									"\" not found.");
							return;
						}
					}
					
					for (Statement c : currentTheorem_.conclusions) {
						Statement result = new Statement(c.getExpression().substitute(substitutions),
								parents,
								null);
						mainWindow_.addStatement(result);
					}
					
				}});
			
			final TableLayout layout = new TableLayout(14,1);
			this.setLayout(layout);
			
			JSeparator separator = new JSeparator();
			separator.setPreferredSize(new Dimension(0,VERT_SPACE_ABOVE_THEOREM_APPLY_BUTTON));
			
			this.add(currentTheoremJLabel_);
			this.add(new JLabel(" "));
			this.add(separator);
			this.add(new JLabel("IF:"));
			JScrollPane hypothesesScrollPane = new JScrollPane(hypothesesList);
			hypothesesScrollPane.setVerticalScrollBar(hypothesesScrollPane.createVerticalScrollBar());
			hypothesesScrollPane.setPreferredSize(new Dimension(HYPOTHESES_SCROLLPANE_WIDTH,HYPOTHESES_SCROLLPANE_HEIGHT));
			this.add(hypothesesScrollPane);
			
			this.add(new JLabel("THEN:"));
			JScrollPane conclusionsScrollPane = new JScrollPane(conclusionsList);
			conclusionsScrollPane.setVerticalScrollBar(conclusionsScrollPane.createVerticalScrollBar());
			conclusionsScrollPane.setPreferredSize(new Dimension(CONCLUSIONS_SCROLLPANE_WIDTH,CONCLUSIONS_SCROLLPANE_HEIGHT));
			this.add(conclusionsScrollPane);
			this.add(new JLabel(" "));
			
			this.add(new JLabel("Context"));
			this.add(pairingsScrollPane);
			
			pairButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Drawables selectedDrawables = TheoremPanel.this.mainWindow_.getSketchCanvas().getSelectedDrawables();
					int numDrawablesSelected = selectedDrawables.size();
					
					Statement selectedStatement = TheoremPanel.this.mainWindow_.getStatementPanel().getSelectedStatement();
					
					if ((numDrawablesSelected == 0) && (selectedStatement != null)) {
						int indexOfSelectedPairing = pairingsList_.getSelectedIndex();
						
						/*
						Expression originalSelectedExpression = selectedStatement.getExpression().getSelectedSubExpression();
						Expression copyOfSelectedExpression = originalSelectedExpression.duplicate();
						copyOfSelectedExpression.deselectRecursive();
						pairings_.get(indexOfSelectedPairing).pair(copyOfSelectedExpression);
						*/
						
						//replaced with...
						pairings_.get(indexOfSelectedPairing).pair(selectedStatement.getExpression().getSelectedSubExpression());
						
						update();
						String status = "pairing expression...";
						TheoremPanel.this.mainWindow_.setInstructionsText(status);
					}
					else if ((numDrawablesSelected == 1) && (selectedStatement == null)) {
						String status = "will attempt to pair drawable..." + "\n" +
						        "number of drawables selected = " + numDrawablesSelected + "\n" +
						        "selectedStatement = " + selectedStatement;
						TheoremPanel.this.mainWindow_.setInstructionsText(status);
						int indexOfSelectedPairing = pairingsList_.getSelectedIndex();
						pairings_.get(indexOfSelectedPairing).pair(Expression.parse(selectedDrawables.get(0).expression()));
						update();
						status = "pairing drawable...";
						TheoremPanel.this.mainWindow_.setInstructionsText(status);
					}
					else {
						JOptionPane.showMessageDialog(null,
								  "You have too many things selected. You can only pair \nthis variable with one object or expression.",
								  "Whoops...",  
								  JOptionPane.ERROR_MESSAGE); 
						String status = "wrong number of things selected..." + "\n" +
						        "number of drawables selected = " + numDrawablesSelected + "\n" +
						        "selectedStatement = " + selectedStatement;
						TheoremPanel.this.mainWindow_.setInstructionsText(status);

						TheoremPanel.this.mainWindow_.setInstructionsText(status);
					}
				}});
			this.add(pairButton);
			
			this.add(separator);
			
			this.add(applyTheoremButton);
		
		}
	
	}
