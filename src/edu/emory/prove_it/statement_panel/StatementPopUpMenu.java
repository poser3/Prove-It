package edu.emory.prove_it.statement_panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import edu.emory.prove_it.MainWindow;
import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.Manipulator;
import edu.emory.prove_it.expression.OperatorExpression;
import edu.emory.prove_it.expression.Operators;
import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.theorem.Pairing;
import edu.emory.prove_it.util.DialogHandler;


@SuppressWarnings("serial")
public class StatementPopUpMenu extends JPopupMenu {
	
    JMenuItem addToBothSidesItem;
    JMenuItem subtractFromBothSidesItem;
    JMenuItem multiplyBothSidesItem;
    JMenuItem divideBothSidesItem;
    JMenuItem hideItem;
    JMenuItem showHiddenItem;
    JMenuItem substituteItem;
    JMenuItem pairItem;
    JMenuItem simplifyItem;
    JMenuItem distributeItem;
    JMenuItem factorItem;
    JMenuItem commuteItem;
    JMenuItem dropParensItem;
    JMenuItem rearrangeRegroupItem;
    JMenuItem multiplyByRecipricalItem;
    JMenuItem cancelFactorItem;
    MainWindow mainWindow_;
    StatementPanel statementPanel_;
    Statement selectedStatement_;
    Expression selectedSubExpression_;
    int indexClicked_;
    Statement statementClicked_;
    Expression leftSideExpression_;
    Expression rightSideExpression_;
    
    //////////////////
    // Constructors //
    //////////////////
    
    private String wrapIfNeeded(Expression expression) {
    	if (expression instanceof OperatorExpression) {
    		return "(" + expression.toString() + ")";
    	}
    	else {
    		return expression.toString();
    	}
    }
  
    
    public StatementPopUpMenu(MainWindow mainWindow, MouseEvent mouseEvent) {
    	
    	mainWindow_ = mainWindow;

    	statementPanel_ = mainWindow_.getStatementPanel();
    	
    	indexClicked_ = statementPanel_.getStatementList().locationToIndex(mouseEvent.getPoint());
    	
    	if (indexClicked_ != -1) {
    		statementClicked_ = statementPanel_.getStatements().get(indexClicked_);
    		boolean statementClickedIsEquation = statementClicked_.getExpression().isEquation();
        	if (statementClickedIsEquation) {
        		leftSideExpression_ = ((OperatorExpression) statementClicked_.getExpression()).getArgs().get(0);
            	rightSideExpression_ = ((OperatorExpression) statementClicked_.getExpression()).getArgs().get(1);
        	}
    	}
    	
    	selectedStatement_ = statementPanel_.getSelectedStatement();
    	if (selectedStatement_ != null) {
    		selectedSubExpression_ = selectedStatement_.getExpression().getSelectedSubExpression();
    	}
    	
    	boolean canDoSomethingToBothSides = (statementClicked_ != null)  &&
    			                            statementClicked_.getExpression().isEquation() &&  
                                            (selectedStatement_ != null);
      	
    	System.out.println("left side = " + leftSideExpression_);
    	System.out.println("right side = " + rightSideExpression_);
    	System.out.println("selected subexpression = " + selectedSubExpression_);
    	
    	/*
    	 * The pairItem is only enabled when (1) a statement is selected, (2) a
    	 * pairing from a theorem is selected, and (3) the type of the selected
    	 * subexpression matches the type of the variable in the theorem
    	 * pairing.
    	 * 
    	 * If we add multiple selection to the statement list then we must
    	 * TODO: make sure that only one statement is selected as we try to pair.
    	 */
    	pairItem = new JMenuItem("Pair with selected theorem variable");
    	final Pairing selectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedValue();
    	pairItem.setEnabled((selectedSubExpression_ != null) && (selectedPairing != null)
    			&& (selectedSubExpression_.getType() == selectedPairing.getVariableExpression().getType()));
    	pairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPairing.pair(selectedSubExpression_);
				
				mainWindow_.getTheoremPanel().update();
				mainWindow_.setInstructionsText("Pairing expression...");
			}});
    	add(pairItem);
    	
    	simplifyItem = new JMenuItem("Evaluate");
    	simplifyItem.setEnabled(selectedSubExpression_ != null);
    	simplifyItem.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			if (selectedSubExpression_ instanceof OperatorExpression) {
    				Expression simplifiedSubExpression = ((OperatorExpression) selectedSubExpression_).simplify();
    				System.out.println("simplified version: " + simplifiedSubExpression.toString());
    				if (! simplifiedSubExpression.equals(selectedSubExpression_)) {
    					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(simplifiedSubExpression);
    					//TODO: add logicParents and geometryParents..
    					mainWindow_.addStatementAndSelect(result, true);
    				}
    			}
    		}
    	});
    	add(simplifyItem);
    	
    	distributeItem = new JMenuItem("Distribute");
    	distributeItem.setEnabled(true);
    	distributeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression distributedVersion = Manipulator.distribute(selectedSubExpression_);
				if (distributedVersion != null) {
					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(distributedVersion);
					//TODO: add logicParents and geometryParents..
					mainWindow_.addStatementAndSelect(result, true);
				}
				else {
					JOptionPane.showMessageDialog(null,
							  "I don't know how to distribute anything here.",
							  "Whoops!",  
							  JOptionPane.ERROR_MESSAGE); 
				}
			}
    	});
    	add(distributeItem);
    	
    	factorItem = new JMenuItem("Factor");
    	factorItem.setEnabled(true);
    	factorItem.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			Expression expr = selectedStatement_.getExpression();
    			if (expr instanceof OperatorExpression) {
    				OperatorExpression parent = selectedSubExpression_.getParent();
    				OperatorExpression grandparent = parent.getParent();
    				Expression factoredVersion = Manipulator.factor(selectedSubExpression_, mainWindow_.getVariableEnvironment());
    				if (factoredVersion != null && parent != null && grandparent != null) {
    					selectedSubExpression_.setSelected(false);
    					(parent.getOp().equals(Operators.named("*")) ? grandparent : parent).setSelected(true);
    					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(factoredVersion);
    					//TODO: add logicParents and geometryParents..
    					mainWindow_.addStatementAndSelect(result,true);
    				}
    				else {
    					JOptionPane.showMessageDialog(null,
    							  "I don't know how to factor this out of its parent expression.",
    							  "Whoops!",  
    							  JOptionPane.ERROR_MESSAGE); 
    				}
    			}
    		}
    	});
    	add(factorItem);
    	
    	
    	commuteItem = new JMenuItem("Commute");
    	commuteItem.setEnabled(true);
    	commuteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression commutedVersion = Manipulator.commute(selectedSubExpression_);
				if (commutedVersion != null) {
					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(commutedVersion);
					//TODO: add logicParents and geometryParents..
					mainWindow_.addStatementAndSelect(result, true);
				}
				else {
					JOptionPane.showMessageDialog(null,
							  "I don't know how to commute anything here.",
							  "Whoops!",  
							  JOptionPane.ERROR_MESSAGE); 
				}
			}
		});
    	add(commuteItem);
    	
    	//TODO: DropParens is not done yet!!! Finish it!!!
    	dropParensItem = new JMenuItem("Drop Parentheses");
    	dropParensItem.setEnabled(true);
    	dropParensItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression droppedParensVersion = Manipulator.dropParensOnSum(selectedSubExpression_);
				if (droppedParensVersion != null) {
					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(droppedParensVersion);
					//TODO: add logicParents and geometryParents..
					mainWindow_.addStatementAndSelect(result, true);
				}
				else {
					JOptionPane.showMessageDialog(null,
							  "I don't know how to legally drop the parentheses here.",
							  "Whoops!",  
							  JOptionPane.ERROR_MESSAGE); 					
				}
			}
		});
    	add(dropParensItem);
    	
    	rearrangeRegroupItem = new JMenuItem("Rearrange/Regroup");
    	rearrangeRegroupItem.setEnabled(true);
    	rearrangeRegroupItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String numberedArgsLatex = Manipulator.getNumberedArgsLatex(selectedSubExpression_,mainWindow_.getVariableEnvironment());
				String newArgOrderAndGrouping = DialogHandler.showArrangementAndGroupingDialog(numberedArgsLatex);
				System.out.println(newArgOrderAndGrouping);
				Expression rearrangedRegroupedExpression = Manipulator.argsToExpression(selectedSubExpression_,newArgOrderAndGrouping);
				System.out.println("selected expression: " + selectedSubExpression_.toString());
				System.out.println("rearranged and regrouped expression: " + rearrangedRegroupedExpression.toString());
				Statement result = selectedStatement_.substituteSelectedIntoDuplicate(rearrangedRegroupedExpression);
				//TODO: add logicParents and geometryParents..
				mainWindow_.addStatementAndSelect(result, true);
			}});
    	add(rearrangeRegroupItem);
    	
    	multiplyByRecipricalItem = new JMenuItem("Apply Definition of Divison");
    	multiplyByRecipricalItem.setEnabled(true);
    	multiplyByRecipricalItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression defAppliedVersion = Manipulator.applyDefinitionOfDivision(selectedSubExpression_,mainWindow_.getVariableEnvironment());
				if (defAppliedVersion != null) {
					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(defAppliedVersion);
					//TODO: add logicParents and geometryParents..
					mainWindow_.addStatementAndSelect(result, true);
				}
				else {
					JOptionPane.showMessageDialog(null,
							  "I don't know how to legally apply the definition of division here.",
							  "Whoops!",  
							  JOptionPane.ERROR_MESSAGE); 					
				}
			}});
    	add(multiplyByRecipricalItem);
    	
    	cancelFactorItem = new JMenuItem("Cancel Factor");
    	cancelFactorItem.setEnabled(true);
    	cancelFactorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Expression expr = selectedStatement_.getExpression();
				OperatorExpression parent = expr.getSelectedSubExpression().getParent();
				OperatorExpression grandparent = parent.getParent();
    			Expression cancelledVersion = Manipulator.cancelCommonFactor(selectedSubExpression_, mainWindow_.getVariableEnvironment());
    			selectedSubExpression_.setSelected(false);
    			(parent.getOp().equals(Operators.named("*")) ? grandparent : parent).setSelected(true);
				//TODO: add logicParents and geometryParents..
				if (cancelledVersion != null) {
					Statement result = selectedStatement_.substituteSelectedIntoDuplicate(cancelledVersion);
					//TODO: add logicParents and geometryParents..
					mainWindow_.addStatementAndSelect(result, true);
				}
				else {
					JOptionPane.showMessageDialog(null,
							  "I don't know how to cancel a common factor here.",
							  "Whoops!",  
							  JOptionPane.ERROR_MESSAGE); 					
				}
			}});
    	add(cancelFactorItem);
    	
    	hideItem = new JMenuItem("Hide Selected Statements");
    	hideItem.setEnabled(statementClicked_ != null);
    	hideItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statementPanel_.hideSelectedStatements();
				statementPanel_.revalidate();
			}});
    	add(hideItem);
    	
    	showHiddenItem = new JMenuItem("Show All Hidden Statements");
    	showHiddenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statementPanel_.showHiddenStatements();	
			}});
    	add(showHiddenItem);
    	
    	this.addSeparator();
    	
    	substituteItem = new JMenuItem("Substitute");
    	substituteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow_.substitute();	
			}});
		add(substituteItem);
		
		this.addSeparator();
    	
    	
		// TODO: For adding, subtracting, multiplying, and dividing both sides, enable menu items only if 
    	// clicked item is an equation AND selected item is a reasonable expression to this end.
    	// The former has been done (possibly poorly), the latter still needs to be added.
		
		// TODO: What if user attempts to divide by zero? ...how do we check if subexpression is
		// zero? ...or deducibly zero?
    	
    	//add menu item for adding selected expression to both sides of clicked expression
    	addToBothSidesItem = new JMenuItem("Add selected expression on both sides");
    	addToBothSidesItem.setEnabled(canDoSomethingToBothSides);
    	addToBothSidesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String statementString = "= (+ " + wrapIfNeeded(leftSideExpression_) + " " + wrapIfNeeded(StatementPopUpMenu.this.selectedSubExpression_) + ") " + 
						                   "(+ " + wrapIfNeeded(rightSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ")";
				System.out.println("new statement = " + statementString);
				statementPanel_.getStatements().addElement(new Statement(statementString, mainWindow_.getVariableEnvironment()));
			}});
    	add(addToBothSidesItem);
    	
    	//add menu item for subtracting selected expression to both sides of clicked expression
        subtractFromBothSidesItem = new JMenuItem("Subtract selected expression from both sides");
        subtractFromBothSidesItem.setEnabled(canDoSomethingToBothSides);   	
        subtractFromBothSidesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String statementString = "= (- " + wrapIfNeeded(leftSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ") " + 
                                           "(- " + wrapIfNeeded(rightSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ")";
				System.out.println("new statement = " + statementString);
				statementPanel_.getStatements().addElement(new Statement(statementString, mainWindow_.getVariableEnvironment()));
			}});
    	add(subtractFromBothSidesItem);
    	
    	//add menu item for subtracting selected expression to both sides of clicked expression
        multiplyBothSidesItem = new JMenuItem("Multiply both sides by selected expression");
        multiplyBothSidesItem.setEnabled(canDoSomethingToBothSides);   	
        multiplyBothSidesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String statementString = "= (* " + wrapIfNeeded(leftSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ") " + 
                                           "(* " + wrapIfNeeded(rightSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ")";
				System.out.println("new statement = " + statementString);
				statementPanel_.getStatements().addElement(new Statement(statementString, mainWindow_.getVariableEnvironment()));
			}});
    	add(multiplyBothSidesItem);
    	
    	//add menu item for subtracting selected expression to both sides of clicked expression
        divideBothSidesItem = new JMenuItem("Divide both sides by selected expression");
        divideBothSidesItem.setEnabled(canDoSomethingToBothSides);   	
        divideBothSidesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String statementString = "= (/ " + wrapIfNeeded(leftSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ") " + 
                                           "(/ " + wrapIfNeeded(rightSideExpression_) + " " + wrapIfNeeded(selectedSubExpression_) + ")";
				System.out.println("new statement = " + statementString);
				statementPanel_.getStatements().addElement(new Statement(statementString, mainWindow_.getVariableEnvironment()));
			}});
    	add(divideBothSidesItem);
    }
}
