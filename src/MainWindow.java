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
	
	private final int STATEMENT_PANEL_WIDTH = 460;
	private final int STATEMENT_PANEL_HEIGHT = 590;
	
	private final int EXPRESSION_SCROLLPANE_WIDTH = 360;
	private final int EXPRESSION_SCROLLPANE_HEIGHT = 580;
	
	private final int TABBED_PANE_WIDTH = 260;
	private final int TABBED_PANE_HEIGHT = 330;
	
	private final int RIGHT_PANEL_WIDTH = 280;
	private final int RIGHT_PANEL_HEIGHT = 600;
	
	private final static boolean PRETTY_PRINT = true;
	private final static JFileChooser fileChooser = new JFileChooser();
	
	private final ExpressionListModel expressions = new ExpressionListModel();
	private final JList<Expression> expressionsList = new JList<Expression>(expressions);
	private final JTextArea instructions = new JTextArea(10, 10);
	
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
				public void actionPerformed(ActionEvent event) {
					for (Object selected : expressionsList.getSelectedValuesList()) {
						Expression e = (Expression) selected;
						
						if (e instanceof OperatorExpression) {
							Expression simplified = ((OperatorExpression) e).simplify();
							if (! simplified.equals(e))
								addExpressionAndSelect(simplified, true);
						}
					}
				}
			});
			add(simplifyButton, "gridwidth="+COLUMNS);
		}
		
		private void addButton(final String opName, final String text) {
			final Operator op = Operator.named(opName);
			final JButton button = new JButton(text);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final Expression selected = getSelected();
					if (selected == null) {
						setInstructionsText("Select an expression from the list.");
						return;
					}
					final Expression fromField = Expression.parse(textField.getText());
					if (fromField == null) {
						setInstructionsText("Enter an expression below.");
						return;
					}
					Expression changed;
					if (selected instanceof OperatorExpression && ((OperatorExpression) selected).getOp() == Operator.named("=")) {
						final ArrayList<Expression> args = ((OperatorExpression) selected).getArgs();
						ArrayList<Expression> newArgs = new ArrayList<Expression>();
						for (Expression arg : args)
							newArgs.add(arg.applyRight(op, fromField));
						changed = new OperatorExpression("=", newArgs);
						
					} else
						changed = selected.applyRight(op, fromField);
					addExpressionAndSelect(changed, true);
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
			
			JButton subExpressionButton = new JButton("Select a sub-expression");
			subExpressionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (getSelected() instanceof OperatorExpression) {
						ExpressionSelectionDialog dialog = new ExpressionSelectionDialog((OperatorExpression) getSelected());
						dialog.setVisible(true);
						Expression selected = dialog.getSelected();
						if (selected != null)
							addExpressionAndSelect(selected, true);
					}
				}
			});
			add(subExpressionButton, "gridwidth="+COLUMNS);
			
			JButton newButton = new JButton("Equation from input");
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					addExpressionAndSelect(textField.getText(), true);
				}
			});
			add(newButton, "gridwidth="+COLUMNS);
			
			add(textField, "gridwidth="+COLUMNS);
		}
		
		private void addButton(final String opName, final String buttonText, final String selectName) {			
			final String[] comboItems = {selectName+" both sides", selectName+" left side", selectName+" right side"};
			final JComboBox<String> comboBox = new JComboBox<String>(comboItems);
			add(comboBox);
			
			final Operator op = Operator.named(opName);
			final JButton button = new JButton(buttonText);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final Expression selected = getSelected();
					if (selected == null) {
						setInstructionsText("Select an expression from the list.");
						return;
					}
					final Expression fromField = Expression.parse(textField.getText());
					if (fromField == null) {
						setInstructionsText("Enter an expression below.");
						return;
					}
					String mode = (String) comboBox.getSelectedItem();
					if (selected instanceof OperatorExpression && ((OperatorExpression) selected).getOp() == Operator.named("=")) {
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
						addExpressionAndSelect(new OperatorExpression("=", newArgs), true);
					}
				}
			});
			add(button);
		}
	}
	private class ManipulatePanel extends JPanel {
		ManipulatePanel() {
			setLayout(new TableLayout(0, 1));
			
			JButton hideButton = new JButton("Hide selected statements");
			hideButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					for (Object selected : expressionsList.getSelectedValuesList()) {
						((Expression) selected).setHidden(true);
					}
					MainWindow.this.update();
				}
			});
			add(hideButton);
			
			JButton unhideButton = new JButton("Show all hidden statements");
			unhideButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					for (Object ex : expressions.toArray()) {
						((Expression) ex).setHidden(false);
					}
					MainWindow.this.update();
				}
			});
			add(unhideButton);
			
			JButton theoremButton = new JButton("Apply theorem");
			theoremButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					TheoremChooserDialog chooser = new TheoremChooserDialog(MainWindow.this);
					chooser.setVisible(true);
					Theorem theorem = chooser.getSelected();
					if (theorem == null) {
						setInstructionsText("You have to choose a theorem.");
						return;
					}
					
					HashMap<String, String> substitutions = new HashMap<String, String>();
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
					
					for (Expression h : theorem.hypotheses) {
						Expression postReplacement = h.substitute(substitutions);
						boolean found = false;
						for (Object e : expressions.toArray()) {
							if (((Expression) e).equals(postReplacement)) {
								found = true;
								break;
							}
						}
						if (! found) {
							setInstructionsText("Hypothesis \"" + 
									postReplacement.toLatex() +
									"\" not found.");
							return;
						}
					}
					for (Expression c : theorem.conclusions)
						addExpression(c.substitute(substitutions));
				}
			});
			add(theoremButton);
			
			JButton reloadTheoremsButton = new JButton("Reload theorems");
			reloadTheoremsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Theorem.loadTheorems();
				}
			});
			add(reloadTheoremsButton);
			
			JButton loadButton = new JButton("Load statements...");
			loadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					int choice = fileChooser.showOpenDialog(MainWindow.this);
					if (choice == JFileChooser.APPROVE_OPTION) {
						try {
							Scanner reader = new Scanner(fileChooser.getSelectedFile());
							while (reader.hasNextLine())
								addExpression(reader.nextLine());
							reader.close();
						} catch (FileNotFoundException e) {
							setInstructionsText("File not found!");
						}
					}
				}
			});
			add(loadButton);
			
			JButton substituteButton = new JButton("Substitute");
			substituteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					final Expression selected = getSelected();
					if (selected == null) {
						setInstructionsText("Select an expression to substitute into.");
						return;
					}
					if (expressions.size() < 2) {
						setInstructionsText("You can't substitute with only one expression.");
						return;
					}
					
					SubstitutionSelectionDialog dialog = new SubstitutionSelectionDialog(MainWindow.this, selected);
					dialog.setVisible(true);
					
					Expression replace = dialog.getFrom();
					Expression with = dialog.getTo();
					if (replace != null && with != null) {
						Expression after = selected.substitute(replace, with);
						if (! after.equals(selected))
							addExpressionAndSelect(selected.substitute(replace, with), true);
						else
							setInstructionsText("No substitution was made.");
					}
					else
						setInstructionsText("You have to actually choose an expression.");
				}
			});
			add(substituteButton);
		}
	}
	
	public void init() {
		
		if (PRETTY_PRINT) {
			expressionsList.setCellRenderer(new ExpressionListCellRenderer());
			expressionsList.setPrototypeCellValue(null);
			expressionsList.setFixedCellHeight(-1);
		}
		
		SketchCanvas sketchCanvas = new SketchCanvas(this,SKETCH_CANVAS_WIDTH,SKETCH_CANVAS_HEIGHT);
		SketchPanel sketchPanel = new SketchPanel();
		sketchPanel.setSketchCanvas(sketchCanvas);
		sketchCanvas.setSketchPanel(sketchPanel);
		this.add(sketchCanvas);
		
		//this.getRegionPanel(EAST).setLayout(new TableLayout(1,1));
		JPanel rightPanel = new JPanel();
		JPanel statementPanel = new JPanel();
		statementPanel.setLayout(new TableLayout(1,1));
		rightPanel.setLayout(new TableLayout(2,1));
		statementPanel.setPreferredSize(new Dimension(STATEMENT_PANEL_WIDTH,STATEMENT_PANEL_HEIGHT));
		rightPanel.setPreferredSize(new Dimension(RIGHT_PANEL_WIDTH,RIGHT_PANEL_HEIGHT));
		
		JScrollPane expressionsScrollPane = new JScrollPane(expressionsList);
		expressionsScrollPane.setVerticalScrollBar(expressionsScrollPane.createVerticalScrollBar());
		expressionsScrollPane.setPreferredSize(new Dimension(EXPRESSION_SCROLLPANE_WIDTH,EXPRESSION_SCROLLPANE_HEIGHT));
		statementPanel.add(expressionsScrollPane);
		
		setInstructionsText("Push the button, Max!");
		instructions.setEditable(false);
		rightPanel.add(instructions);
				
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(TABBED_PANE_WIDTH,TABBED_PANE_HEIGHT));
		tabbedPane.addTab("Operate", new OperatePanel());
		tabbedPane.addTab("Create", new CreatePanel());
		tabbedPane.addTab("Manipulate", new ManipulatePanel());
		tabbedPane.addTab("Geometry", sketchPanel);
		rightPanel.add(tabbedPane);
		
		this.add(statementPanel, EAST);
		this.add(rightPanel, WEST);
		
		setSize(MAIN_WINDOW_WIDTH,MAIN_WINDOW_HEIGHT);
		
		this.getCentralRegionSize().getWidth();
	}
	
	private void update() {
		expressionsList.update(expressionsList.getGraphics());
	}
	
	public void setInstructionsText(String s) {
		instructions.setText(s);
	}
	
	public Object[] getExpressions() {
		return expressions.toArray();
	}
	public boolean isSelectionEmpty() {
		return expressionsList.isSelectionEmpty();
	}
	public Expression getSelected() {
		return (Expression) expressionsList.getSelectedValue();
	}
	
	public void addExpression(final String s) {
		expressions.addElement(Expression.parse(s));
	}
	public void addExpression(Expression e) {
		expressions.addElement(e);
		if (! expressions.contains(e))
			throw new IllegalArgumentException();
	}
	public void addExpressionAndSelect(final String s, boolean shouldScroll) {
		Expression e = Expression.parse(s);
		expressions.addElement(e);
		expressionsList.setSelectedValue(e, shouldScroll);
	}
	public void addExpressionAndSelect(Expression e, boolean shouldScroll) {
		expressions.addElement(e);
		expressionsList.setSelectedValue(e, shouldScroll);
	}
	
}

