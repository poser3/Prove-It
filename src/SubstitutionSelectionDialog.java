import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import acm.gui.TableLayout;

@SuppressWarnings("serial")
public class SubstitutionSelectionDialog extends JDialog {
	
	private final Expression substitutingIn;
	private final JLabel previewLabel;
	private final SubstitutionCellRenderer renderer = new SubstitutionCellRenderer();
	private final JComboBox<OperatorExpression> selectionBox;
	private final JCheckBox checkBox;
	private boolean choiceMade = true;
	
	public SubstitutionSelectionDialog(MainWindow owner, Expression substitutingIn) {
		super();
		setSize(400, 300);
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		this.substitutingIn = substitutingIn;
		previewLabel = new JLabel(substitutingIn.toLatex());
		
		Object[] expressionsFromMainWindow = owner.getExpressions();
		Vector<OperatorExpression> expressions = new Vector<OperatorExpression>();
		for (int i = 0; i<expressionsFromMainWindow.length; i++) {
			Object ex = expressionsFromMainWindow[i];
			if (ex instanceof OperatorExpression
					&& ! substitutingIn.equals(ex)
					&& ((OperatorExpression) ex).getOp().equals(Operator.named("=")))
				expressions.add((OperatorExpression) ex);
		}		
		
		selectionBox = new JComboBox<OperatorExpression>(expressions);
		selectionBox.setRenderer(renderer);
		selectionBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				updatePreview();
			}
		});
		
		checkBox = new JCheckBox("Reverse");
		checkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				renderer.reversed = !renderer.reversed;
				selectionBox.update(selectionBox.getGraphics());
				updatePreview();
			}
		});
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				choiceMade = true;
				setVisible(false);
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				choiceMade = false;
				setVisible(false);
			}
		});
				
		setLayout(new TableLayout(6, 2, 10, 10));
		add(new JLabel(substitutingIn.toLatex()), "gridwidth=2");
		add(new JLabel("becomes"), "gridwidth=2");
		add(previewLabel, "gridwidth=2");
		add(new JLabel());
		add(checkBox);
		add(selectionBox, "gridwidth=2");
		add(okButton);
		add(cancelButton);
		
	}
	
	public Expression getFrom() {
		if (choiceMade) {
			OperatorExpression ex = (OperatorExpression) selectionBox.getSelectedItem();
			return renderer.reversed ? ex.getArg(1) : ex.getArg(0);
		}
		
		return null;
	}
	public Expression getTo() {
		if (choiceMade) {
			OperatorExpression ex = (OperatorExpression) selectionBox.getSelectedItem();
			return renderer.reversed ? ex.getArg(0) : ex.getArg(1);
		}
		
		return null;
	}
	
	private void updatePreview() {
		previewLabel.setText(substitutingIn.substitute(getFrom(), getTo()).toLatex());
	}

}

