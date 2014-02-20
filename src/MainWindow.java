import java.awt.Dimension;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.*;

import acm.gui.TableLayout;
import acm.program.Program;

@SuppressWarnings("serial")
public class MainWindow extends Program {
	
	private final int MAIN_WINDOW_WIDTH = 1200;
	private final int MAIN_WINDOW_HEIGHT = 600;
	
	private final int SKETCH_CANVAS_WIDTH = 450;
	private final int SKETCH_CANVAS_HEIGHT = 600;
	
	private final int TABBED_PANE_WIDTH = 260;
	private final int TABBED_PANE_HEIGHT = 480;
	
	private final int RIGHT_PANEL_WIDTH = 280;
	private final int RIGHT_PANEL_HEIGHT = 600;
	
	private final static JFileChooser fileChooser = new JFileChooser();
	
	private final JTextArea instructions = new JTextArea(5, 10);
	private JTabbedPane tabbedPane;
	private TheoremPanel theoremPanel;
	private OperatePanel operatePanel;
	private CreatePanel createPanel;
	public static JTextArea log;
	private MainMenuBar menuBar;
	private SketchCanvas sketchCanvas;
	private StatementPanel statementPanel = new StatementPanel(this);
	
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
					addStatementAndSelect(textField.getText(), true);
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
		
		/*
		 * The LatexHandler takes a few seconds to initialize.
		 * By doing this here, it looks like part of the general loading process,
		 * instead of a problem with statement creation.
		 */
		LatexHandler.latexToImage("hello world");
		
		sketchCanvas = new SketchCanvas(this, SKETCH_CANVAS_WIDTH, SKETCH_CANVAS_HEIGHT);
		SketchPanel sketchPanel = new SketchPanel();
		sketchPanel.setSketchCanvas(sketchCanvas);
		sketchCanvas.setSketchPanel(sketchPanel);
		this.add(sketchCanvas);
		this.add(sketchPanel,SOUTH);
		
		JPanel rightPanel = new JPanel();
		
		rightPanel.setLayout(new TableLayout(2,1));		
		rightPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH,RIGHT_PANEL_HEIGHT));
		
		
		setInstructionsText("Push the button, Max!");
		instructions.setEditable(false);
		rightPanel.add(instructions);
				
		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(TABBED_PANE_WIDTH,TABBED_PANE_HEIGHT));
		theoremPanel = new TheoremPanel(this);
		operatePanel = new OperatePanel();
		createPanel = new CreatePanel();
		tabbedPane.addTab("Operate", operatePanel);
		tabbedPane.addTab("Create", createPanel);
		tabbedPane.addTab("Theorem", theoremPanel);
		rightPanel.add(tabbedPane);
		
		this.add(statementPanel, EAST);
		this.add(rightPanel, WEST);
		
		setSize(MAIN_WINDOW_WIDTH,MAIN_WINDOW_HEIGHT);
		
		menuBar = new MainMenuBar(this);
		this.setJMenuBar(menuBar);
		
		this.getCentralRegionSize().getWidth();
		
		log = instructions;
		
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
		statementPanel.deselectAllStatements();
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
