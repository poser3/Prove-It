import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.*;

import acm.graphics.GRect;
import acm.gui.TableLayout;
import acm.program.Program;

@SuppressWarnings("serial")
public class MainWindow extends Program {
	
	private final int MAIN_WINDOW_WIDTH = 1000;
	private final int MAIN_WINDOW_HEIGHT = 700;
	
	private final int SKETCH_CANVAS_WIDTH = 450;
	private final int SKETCH_CANVAS_HEIGHT = 400;
	
	private final int TABBED_PANE_WIDTH = 450;
	private final int TABBED_PANE_HEIGHT = 400;
	
	private final static JFileChooser fileChooser = new JFileChooser();
		
	private final JTextArea instructions = new JTextArea(5, 10);
	private JTabbedPane tabbedPane;
	private TheoremPanel theoremPanel;
	private OperatePanel operatePanel;
	private CreatePanel createPanel;
	public static JTextArea log;
	private MainMenuBar menuBar;
	private SketchCanvas sketchCanvas;
	private StatementPanel statementPanel;
	private JPanel drawingPanel;
	public ViewingRectangle viewingRectangle;
	
	private class OperatePanel extends JPanel {
		final private short COLUMNS = 2;
		final JTextField textField = new JTextField();
		
		OperatePanel() {
			final TableLayout layout = new TableLayout(0, COLUMNS);
			layout.setHgap(20);
			layout.setVgap(20);
			setLayout(layout);
			
			addButton("+");
			addButton("-");
			addButton("*");
			addButton("/");
			
			add(textField, "gridwidth="+COLUMNS);
			
			JButton simplifyButton = new JButton("Simplify");
			simplifyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					for (Object selected : statementPanel.getStatementList().getSelectedValuesList()) {
						Statement s = (Statement) selected;
						Expression e = s.getExpression();
						
						if (e instanceof OperatorExpression) {
							Expression simplified = ((OperatorExpression) e).simplify();
							if (! simplified.equals(e)) {
								Statement result = new Statement(simplified, s.logicParents(), s.geometryParents());
								addStatementAndSelect(result, true);
							}
						}
					}
				}
			});
			add(simplifyButton, "gridwidth="+COLUMNS);
		}
		
		private void addButton(final String opName, final String text) {
			final Operator op = Operators.named(opName);
			final JButton button = new JButton(text);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Statement statement = statementPanel.getSelectedStatement();
					if (statement == null) {
						setInstructionsText("Select a statement from the list.");
						return;
					}
					Expression selected = statement.getExpression();
					final Expression fromField = Expression.parse(textField.getText());
					if (fromField == null) {
						setInstructionsText("Enter an expression below.");
						return;
					}
					Expression changed;
					if (selected instanceof OperatorExpression && ((OperatorExpression) selected).getOp() == Operators.named("=")) {
						final ArrayList<Expression> args = ((OperatorExpression) selected).getArgs();
						ArrayList<Expression> newArgs = new ArrayList<Expression>();
						for (Expression arg : args)
							newArgs.add(arg.applyRight(op, fromField));
						changed = new OperatorExpression("=", newArgs);
						
					} else
						changed = selected.applyRight(op, fromField);
					
					Statement result = new Statement(changed, statement.logicParents(), statement.geometryParents());
					addStatementAndSelect(result, true);
				}
			});
			add(button);
		}
		private void addButton(final String name) {
			addButton(name, name);
		}
	}
	private class CreatePanel extends JPanel {
		final private short COLUMNS = 2;
		final JTextField textField = new JTextField();
		
		CreatePanel() {
			final TableLayout layout = new TableLayout(0, COLUMNS);
			layout.setVgap(20);
			setLayout(layout);
			
			addButton("+", "+", "Add to");
			addButton("*", "*", "Multiply");
			
			JButton newButton = new JButton("Equation from input");
			newButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					System.out.println("adding statement and selecting it");
					addStatementAndSelect(textField.getText(), true);
					MainWindow.this.revalidate();
				}
			});
			add(newButton, "gridwidth="+COLUMNS);
			
			add(textField, "gridwidth="+COLUMNS);
		}
		
		private void addButton(final String opName, final String buttonText, final String selectName) {			
			final String[] comboItems = {selectName+" both sides", selectName+" left side", selectName+" right side"};
			final JComboBox<String> comboBox = new JComboBox<String>(comboItems);
			add(comboBox);
			
			final Operator op = Operators.named(opName);
			final JButton button = new JButton(buttonText);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Statement statement = statementPanel.getSelectedStatement();
					if (statement == null) {
						setInstructionsText("Select an expression from the list.");
						return;
					}
					Expression selected = statement.getExpression();
					Expression fromField = Expression.parse(textField.getText());
					if (fromField == null) {
						setInstructionsText("Enter an expression below.");
						return;
					}
					String mode = (String) comboBox.getSelectedItem();
					if (selected instanceof OperatorExpression && ((OperatorExpression) selected).getOp() == Operators.named("=")) {
						final ArrayList<Expression> args = ((OperatorExpression) selected).getArgs();
						ArrayList<Expression> newArgs = new ArrayList<Expression>();
						for (int i=0; i<args.size(); i++) {
							if (mode.endsWith(" both sides"))
								newArgs.add(args.get(i).applyRight(op, fromField));
							else if (mode.endsWith(" left side") && i==0)
								newArgs.add(args.get(i).applyRight(op, fromField));
							else if (mode.endsWith(" right side") && i==args.size()-1)
								newArgs.add(args.get(i).applyRight(op, fromField));
							else
								newArgs.add(args.get(i));
						}
						Statement result = new Statement(new OperatorExpression("=", newArgs), statement.logicParents(), statement.geometryParents());
						addStatementAndSelect(result, true);
					}
				}
			});
			add(button);
		}
	}
	
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
		
		setSize(MAIN_WINDOW_WIDTH,MAIN_WINDOW_HEIGHT);
		
		//construct sketch canvas, where the drawing takes place:
		drawingPanel = new JPanel();
		drawingPanel.setLayout(new BorderLayout());
		JLabel drawingPanelTitle = new JLabel("Figure / Construction");
		drawingPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);;
		sketchCanvas = new SketchCanvas(this,SKETCH_CANVAS_WIDTH,SKETCH_CANVAS_HEIGHT);
		sketchCanvas.setPreferredSize(new Dimension(SKETCH_CANVAS_WIDTH,SKETCH_CANVAS_HEIGHT));
		drawingPanel.add(drawingPanelTitle, NORTH);
		drawingPanel.add(sketchCanvas);
		
		//construct sketchPanel, where buttons for adding drawables are located
		SketchPanel sketchPanel = new SketchPanel();
		sketchPanel.setSketchCanvas(sketchCanvas);
		sketchCanvas.setSketchPanel(sketchPanel);
		
		//construct tabbed area, where variable pairing and theorem application happens
		//TODO: move functionality of non-theorem tabs to various context menus
		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(TABBED_PANE_WIDTH,TABBED_PANE_HEIGHT));
		theoremPanel = new TheoremPanel(this);
		operatePanel = new OperatePanel();
		createPanel = new CreatePanel();
		tabbedPane.addTab("Theorem", theoremPanel);
		tabbedPane.addTab("Operate", operatePanel);
		tabbedPane.addTab("Create", createPanel);
		
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
		JLabel variablePanelTitle = new JLabel("Variables Defined");
		variablePanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		VariablePanel variableListPanel = new VariablePanel(); //stub for variable panel
		variablePanel.add(variablePanelTitle, NORTH);
		variablePanel.add(variableListPanel);
		//DEBUG: put some sample variables in the list
		variableListPanel.addVariable(new VariableExpression("realValue","theta","\\theta"));  //args = type, name, latex
		variableListPanel.addVariable(new VariableExpression("point","A","A"));  //args = type, name, latex
		variableListPanel.addVariable(new VariableExpression("line","L1","l_1"));  //args = type, name, latex
		
		//add the panels and setup the layout manager
		this.getRegionPanel(NORTH).add(sketchPanel);
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
		addStatement(new Statement(s));
	}
		
	public void addStatementAndSelect(Statement s, boolean shouldScroll) {
		statementPanel.deselectAllStatements();
		addStatement(s);
		s.getExpression().setSelected(true);
		statementPanel.getStatementList().setSelectedValue(s, shouldScroll);
	}
	
	public void addStatementAndSelect(final String s, boolean shouldScroll) {
		addStatementAndSelect(new Statement(s), shouldScroll);
	}
	
	public TheoremPanel getTheoremPanel() {
		System.out.println("getTheoremPanel() returns : " + theoremPanel);
		return theoremPanel;
	}
	
	public void deselectAll() {
		Statement selectedStatement = statementPanel.getStatementList().getSelectedValue();
		selectedStatement.getExpression().deselectRecursive();
		statementPanel.getStatementList().clearSelection();
		sketchCanvas.deselectEverythingInCanvas();
	}
	
}
