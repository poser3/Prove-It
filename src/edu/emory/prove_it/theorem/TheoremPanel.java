package edu.emory.prove_it.theorem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import edu.emory.prove_it.MainWindow;
import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.expression.VariableExpression;
import edu.emory.prove_it.statement_panel.StatementListCellRenderer;
import edu.emory.prove_it.util.SpringUtilities;


@SuppressWarnings("serial")
public class TheoremPanel extends JPanel {
	
		public final int PAIRINGS_SCROLLPANE_WIDTH = 300;
		public final int PAIRINGS_SCROLLPANE_HEIGHT = 150;
		public final int HYPOTHESES_SCROLLPANE_WIDTH = 350;
		public final int HYPOTHESES_SCROLLPANE_HEIGHT = 50;
		public final int CONCLUSIONS_SCROLLPANE_WIDTH = 350;
		public final int CONCLUSIONS_SCROLLPANE_HEIGHT = 50;
		public final int VERT_SPACE_ABOVE_THEOREM_APPLY_BUTTON = 30;
		
		MainWindow mainWindow_;
		JComboBox<String> theoremComboBox_;
		final JButton chooseTheoremButton;
		final JButton applyTheoremButton;
		private DefaultListModel<Pairing> pairings_;
	    private JList<Pairing> pairingsList_;
	    private final DefaultListModel<Statement> hypotheses = new DefaultListModel<Statement>();
		private final DefaultListModel<Statement> conclusions = new DefaultListModel<Statement>();
	    private final JList<Statement> hypothesesList = new JList<Statement>(hypotheses);
		private final JList<Statement> conclusionsList = new JList<Statement>(conclusions);
		private Theorem currentTheorem_;
		
		@Override
		public String toString() {
			return "Theorem panel has current theorem of : " + currentTheorem_;
		}
		
		public DefaultListModel<Pairing> getPairings() {
			return pairings_;
		}
		
		public JList<Pairing> getPairingsList() {
			return pairingsList_;
		}
		
		public void update() {
			pairingsList_.update(pairingsList_.getGraphics());
		}
		
		public void setCurrentTheorem(Theorem theorem) {
			//set current theorem instance variable
			currentTheorem_ = theorem;
			
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
		}
		
		public TheoremPanel(MainWindow mainWindow) {
			mainWindow_ = mainWindow;
			
			String[] theoremStrings = new String[Theorem.theorems.size()];
			
			Theorem.loadTheorems();
			for (int i=0; i < Theorem.theorems.size(); i++) {
				theoremStrings[i] = Theorem.theorems.get(i).toString();
			}
		
			theoremComboBox_ = new JComboBox<String>(theoremStrings);
			theoremComboBox_.setSelectedIndex(0);
			theoremComboBox_.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TheoremPanel.this.setCurrentTheorem(Theorem.theorems.get(theoremComboBox_.getSelectedIndex()));
					System.out.println(TheoremPanel.this); //for DEBUGGING, shows current theorem 
				}});
			
			hypothesesList.setCellRenderer(new StatementListCellRenderer());
			conclusionsList.setCellRenderer(new StatementListCellRenderer());
			
			pairings_ = new DefaultListModel<Pairing>();
			pairingsList_ = new JList<Pairing>(pairings_);
			
			pairingsList_.setCellRenderer(new PairingListCellRenderer());
			JScrollPane pairingsScrollPane = new JScrollPane(pairingsList_);
			pairingsList_.setFocusable(true);
			
			pairingsScrollPane.setVerticalScrollBar(pairingsScrollPane.createVerticalScrollBar());
			//pairingsScrollPane.setPreferredSize(new Dimension(PAIRINGS_SCROLLPANE_WIDTH,PAIRINGS_SCROLLPANE_HEIGHT));
			
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

			JScrollPane hypothesesScrollPane = new JScrollPane(hypothesesList);
			hypothesesScrollPane.setVerticalScrollBar(hypothesesScrollPane.createVerticalScrollBar());
			
			JScrollPane conclusionsScrollPane = new JScrollPane(conclusionsList);
			conclusionsScrollPane.setVerticalScrollBar(conclusionsScrollPane.createVerticalScrollBar());
			

			
			JLabel ifLabel = new JLabel("If:",SwingConstants.RIGHT);
			JLabel thenLabel = new JLabel("Then:",SwingConstants.RIGHT);
			JLabel contextLabel = new JLabel("Context:",SwingConstants.RIGHT);
			
			this.setLayout(new BorderLayout());
			
			JPanel centralTheoremArea = new JPanel();
			this.add(centralTheoremArea);
			
			JPanel topTheoremArea = new JPanel();
			this.add(topTheoremArea,BorderLayout.PAGE_START);
			topTheoremArea.add(theoremComboBox_);
			topTheoremArea.add(applyTheoremButton);
			
			centralTheoremArea.setLayout(new SpringLayout());
			centralTheoremArea.add(ifLabel);
			centralTheoremArea.add(hypothesesScrollPane);
			centralTheoremArea.add(thenLabel);
			centralTheoremArea.add(conclusionsScrollPane);
			centralTheoremArea.add(contextLabel);
			centralTheoremArea.add(pairingsScrollPane);
			SpringUtilities.makeCompactGrid(centralTheoremArea,
					3, 2,  //rows, cols
					5, 5,  //initialX, initialY
					5, 5); //xPad, yPad
			centralTheoremArea.revalidate();
		}
	
	}
