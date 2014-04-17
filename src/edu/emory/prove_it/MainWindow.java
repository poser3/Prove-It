package edu.emory.prove_it;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.*;

import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.expression.VariableEnvironment;
import edu.emory.prove_it.sketch_canvas.SketchCanvas;
import edu.emory.prove_it.sketch_canvas.SketchPanel;
import edu.emory.prove_it.statement_panel.StatementPanel;
import edu.emory.prove_it.theorem.Theorem;
import edu.emory.prove_it.theorem.TheoremChooserDialog;
import edu.emory.prove_it.theorem.TheoremPanel;
import edu.emory.prove_it.util.LatexHandler;
import edu.emory.prove_it.util.ListUtils;
import edu.emory.prove_it.util.SpringUtilities;
import edu.emory.prove_it.util.Tester;
import edu.emory.prove_it.variable_panel.VariablePanel;
import acm.program.Program;

@SuppressWarnings("serial")
public class MainWindow extends Program {
	
	private final int MAIN_WINDOW_WIDTH = 1200;
	private final int MAIN_WINDOW_HEIGHT = 700;
	
	private final int SKETCH_CANVAS_WIDTH = 450;
	private final int SKETCH_CANVAS_HEIGHT = 400;
	
	private final int TABBED_PANE_WIDTH = 450;
	private final int TABBED_PANE_HEIGHT = 400;
	
	private final static JFileChooser fileChooser = new JFileChooser();
	
	private final JTextArea instructions = new JTextArea(5, 10);
	private JTabbedPane tabbedPane;
	private TheoremPanel theoremPanel;
	public static JTextArea log;
	private MainMenuBar menuBar;
	private SketchCanvas sketchCanvas;
	private StatementPanel statementPanel;
	private VariablePanel variableListPanel;
	private JPanel drawingPanel;
	public ViewingRectangle viewingRectangle;
	
	
	
	
	
	public StatementPanel getStatementPanel() {
		return statementPanel;
	}
	
	public void applyTheorem() {
		TheoremChooserDialog chooser = new TheoremChooserDialog(MainWindow.this);
		chooser.setVisible(true);
		Theorem theorem = chooser.getSelected();
		if (theorem == null) {
			setInstructionsText("You have to choose a theorem.");
			return;
		}
		
		HashMap<String, String> substitutions = new HashMap<String, String>();
		
		tabbedPane.setSelectedComponent(theoremPanel);
		
		
		for (Expression v : theorem.variables) {
			String replace = v.toString();
			
			String with = JOptionPane.showInputDialog(
					MainWindow.this,
					"What does "+ replace +" stand for?",
					"Substitute",
					JOptionPane.PLAIN_MESSAGE);
			
			
			if (with == null)
				return;
			substitutions.put(replace, with);
		}
		
		ArrayList<Statement> parents = new ArrayList<Statement>();
		for (Statement h : theorem.hypotheses) {
			Expression postReplacement = h.getExpression().substitute(substitutions);
			boolean found = false;
			for (Object s : statementPanel.getStatements().toArray()) {
				Expression e = ((Statement) s).getExpression();
				if (e.equals(postReplacement)) {
					found = true;
					parents.add((Statement) s);
					break;
				}
			}
			if (! found) {
				setInstructionsText("Hypothesis \"" + 
						postReplacement.toString() +
						"\" not found.");
				return;
			}
		}
		for (Statement c : theorem.conclusions) {
			Statement result = new Statement(c.getExpression().substitute(substitutions),
					parents,
					null);
			addStatement(result);
		}
	}
	
	public void reloadTheorems() {
		Theorem.loadTheorems();
	}
	
	public void loadStatements() {
		int choice = fileChooser.showOpenDialog(MainWindow.this);
		if (choice == JFileChooser.APPROVE_OPTION) {
			try {
				Scanner reader = new Scanner(fileChooser.getSelectedFile());
				while (reader.hasNextLine())
					addStatement(reader.nextLine());
				reader.close();
			} catch (FileNotFoundException e) {
				setInstructionsText("File not found!");
			}
		}
	}
	
	
	public void substitute() {
		final Statement selected = statementPanel.getSelectedStatement();
		if (selected == null) {
			setInstructionsText("Select a statement to substitute into.");
			return;
		}
		if (statementPanel.getStatements().size() < 2) {
			setInstructionsText("You can't substitute with only one statement.");
			return;
		}
		
		SubstitutionSelectionDialog dialog = new SubstitutionSelectionDialog(MainWindow.this, selected);
		dialog.setVisible(true);
		
		Expression replace = dialog.getFrom();
		Expression with = dialog.getTo();
		if (replace != null && with != null) {
			Expression after = selected.getExpression().substitute(replace, with);
			if (! after.equals(selected)) {
				Statement result = new Statement(selected.getExpression().substitute(replace, with),
						ListUtils.listOf(selected, dialog.getChoice()),
						null);
				addStatementAndSelect(result, true);
			}
			else
				setInstructionsText("No substitution was made.");
		}
		else
			setInstructionsText("You have to choose a statement.");
	}
	
	public SketchCanvas getSketchCanvas() {
		return sketchCanvas;
	}
	
	@Override
	public void init() {
		
		/*
		 * The LatexHandler takes a few seconds to initialize.
		 * By doing this here, it looks like part of the general loading process,
		 * instead of a problem with statement creation.
		 */
		LatexHandler.latexToImage("hello world");
		
		setSize(MAIN_WINDOW_WIDTH,MAIN_WINDOW_HEIGHT);
		
		//construct sketch canvas, where the drawing takes place:
		drawingPanel = new JPanel();
		drawingPanel.setLayout(new BorderLayout());
		JLabel drawingPanelTitle = new JLabel("Figure / Construction");
		drawingPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);;
		sketchCanvas = new SketchCanvas(this, SKETCH_CANVAS_WIDTH, SKETCH_CANVAS_HEIGHT);
		sketchCanvas.setPreferredSize(new Dimension(SKETCH_CANVAS_WIDTH,SKETCH_CANVAS_HEIGHT));
		drawingPanel.add(drawingPanelTitle, NORTH);
		drawingPanel.add(sketchCanvas);
		
		//construct sketchPanel, where buttons for adding drawables are located
		SketchPanel sketchPanel = new SketchPanel();
		sketchPanel.setSketchCanvas(sketchCanvas);
		sketchCanvas.setSketchPanel(sketchPanel);
		
		//construct expressionsPanel, where buttons for creating or modifying expressions are located
		ExpressionPanel expressionsPanel = new ExpressionPanel(this);
		
		//construct tabbed area, where variable pairing and theorem application happens
		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(TABBED_PANE_WIDTH,TABBED_PANE_HEIGHT));
		theoremPanel = new TheoremPanel(this);
		tabbedPane.addTab("Theorem", theoremPanel);
		
		//construct statement area, where given and deduced statements are shown
		JPanel deductionsPanel = new JPanel();
		deductionsPanel.setLayout(new BorderLayout());
		JLabel statementPanelTitle = new JLabel("Given and/or Deduced Statements");
		statementPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);;
		statementPanel = new StatementPanel(this);
		deductionsPanel.add(statementPanelTitle, NORTH);
		deductionsPanel.add(statementPanel);
		
		
		//construct variable area, where variable types and definitions are shown
		// TODO: add variable JList here instead of a stubBtn placeholder
		JPanel variablePanel = new JPanel();
		variablePanel.setLayout(new BorderLayout());
		JLabel variablePanelTitle = new JLabel("Variables");
		variablePanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		variableListPanel = new VariablePanel(this); //stub for variable panel
		variablePanel.add(variablePanelTitle, NORTH);
		variablePanel.add(variableListPanel);
		
		//add the panels and setup the layout manager
		this.getRegionPanel(NORTH).add(sketchPanel);
		this.getRegionPanel(NORTH).add(new JSeparator(SwingConstants.VERTICAL));
		this.getRegionPanel(NORTH).add(expressionsPanel);
		
		this.setLayout(new SpringLayout());
		
		this.add(drawingPanel);
		this.add(variablePanel);
		this.add(tabbedPane);
		this.add(deductionsPanel);

		SpringUtilities.makeCompactGrid(this.getRegionPanel(CENTER),
                						2, 2,  //rows, cols
                						5, 5,  //initialX, initialY
                						5, 5); //xPad, yPad

		this.revalidate();
		
		
		//DEBUG INFO:
		System.out.println("SKETCH_CANVAS_WIDTH = " + SKETCH_CANVAS_WIDTH);
		System.out.println("SKETCH_CANVAS_HEIGHT = " + SKETCH_CANVAS_HEIGHT);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// TODO: remove the instructions area. Currently instructions area and instructionsAndTabsPanel just 
		//       commented out below so as not to break anything -- but they ultimately need to be 
		//       removed.
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//JPanel instructionsAndTabsPanel = new JPanel();
		//instructionsAndTabsPanel.setLayout(new TableLayout(2,1));		
		//instructionsAndTabsPanel.setPreferredSize(new Dimension(INST_AND_TABS_PANEL_WIDTH,INST_AND_TABS_PANEL_HEIGHT));
		//setInstructionsText("Push the button, Max!");
		//instructions.setEditable(false);
		//instructionsAndTabsPanel.add(instructions);
		//instructionsAndTabsPanel.add(tabbedPane);
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		menuBar = new MainMenuBar(this);
		this.setJMenuBar(menuBar);
		
		log = instructions;
		
		System.out.println("this.getCentralRegionSize().getWidth() = " + this.getCentralRegionSize().getWidth());
		System.out.println("this.getCentralRegionSize().getHeight() = " + this.getCentralRegionSize().getHeight());
		
		viewingRectangle = new ViewingRectangle(0,this.getCentralRegionSize().getWidth(),
				                                0,this.getCentralRegionSize().getHeight());
		
		sketchCanvas.setViewingRectangle(viewingRectangle);
		
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				sketchCanvas.setViewingRectangle(new ViewingRectangle(0,MainWindow.this.getCentralRegionSize().getWidth(),
                                                                      0,MainWindow.this.getCentralRegionSize().getHeight()));
				sketchCanvas.updateDrawables();
				MainWindow.this.revalidate();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		
		//For Debugging...
		@SuppressWarnings("unused")
		Tester tester = new Tester(this);
	
	}
	
	public void setInstructionsText(String s) {
		instructions.setText(s);
	}
	
	public void appendInstructionsText(String s) {
		instructions.append(s);
	}
	
	public Object[] getStatements() {
		return statementPanel.getStatements().toArray();
	}
	
	public boolean isSelectionEmpty() {
		return statementPanel.getStatementList().isSelectionEmpty();
	}
		
	public void printStatementsToConsole() {
		System.out.println("statements currently look like this (read from getStatements() method):");
		for (int i=0; i<statementPanel.getStatements().getSize(); i++) {
			System.out.println(statementPanel.getStatements().getElementAt(i));
		}
		System.out.println("statements currently look like this (read from getStatementList() method):");
		for (int i=0; i<statementPanel.getStatementList().getModel().getSize(); i++) {
			System.out.println(statementPanel.getStatementList().getModel().getElementAt(i));
		}
	}
	
	public void addStatement(Statement s) {
		System.out.println("adding statement" + s);
		statementPanel.addStatement(s);
		setInstructionsText(s.toString());
	}
	
	public void addStatement(final String s) {
		addStatement(new Statement(s, getVariableEnvironment()));
	}
		
	public void addStatementAndSelect(Statement s, boolean shouldScroll) {
		statementPanel.deselectAllStatements();
		addStatement(s);
		s.getExpression().setSelected(true);
		statementPanel.getStatementList().setSelectedValue(s, shouldScroll);
	}
	
	public void addStatementAndSelect(final String s, boolean shouldScroll) {
		addStatementAndSelect(new Statement(s, getVariableEnvironment()), shouldScroll);
	}
	
	public TheoremPanel getTheoremPanel() {
		System.out.println("getTheoremPanel() returns : " + theoremPanel);
		return theoremPanel;
	}
	
	public VariablePanel getVariablePanel() {
		return variableListPanel;
	}
	
	public VariableEnvironment getVariableEnvironment() {
		return variableListPanel.getEnvironment();
	}
	
	public void deselectAll() {
		Statement selectedStatement = statementPanel.getStatementList().getSelectedValue();
		selectedStatement.getExpression().deselectRecursive();
		statementPanel.getStatementList().clearSelection();
		sketchCanvas.deselectEverythingInCanvas();
	}
	
}
