package edu.emory.prove_it.statement_panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.emory.prove_it.MainWindow;
import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.OperatorExpression;
import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.theorem.Pairing;


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
    	
    	simplifyItem = new JMenuItem("Simplify");
    	simplifyItem.setEnabled(selectedSubExpression_ != null);
    	simplifyItem.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			if (selectedSubExpression_ instanceof OperatorExpression) {
    				Expression simplifiedSubExpression = ((OperatorExpression) selectedSubExpression_).simplify();
    				if (! simplifiedSubExpression.equals(selectedSubExpression_)) {
    					Statement result = selectedStatement_.substitute(selectedSubExpression_, simplifiedSubExpression);
    					//TODO: add logicParents and geometryParents..
    					mainWindow_.addStatementAndSelect(result, true);
    				}
    			}
    		}
    	});
    	add(simplifyItem);
    	
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
