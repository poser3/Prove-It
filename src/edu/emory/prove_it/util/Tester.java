package edu.emory.prove_it.util;
import java.awt.Image;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import acm.graphics.GImage;
import edu.emory.prove_it.MainWindow;
import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.OperatorExpression;
import edu.emory.prove_it.expression.VariableEnvironment;


@SuppressWarnings("unused")
public class Tester {
	
	public void printHashMapOfStrings(HashMap<String, String> map) {
		for (String key: map.keySet()) {
			System.out.println(key + "->" + map.get(key) + ";");
		}
	}
	
	public Tester(MainWindow mainWindow) {
		final VariableEnvironment environment = new VariableEnvironment();
		
		System.out.println("Hi from the tester...");
		OperatorExpression oe1 = (OperatorExpression) (Expression.parse("= (+ a b c) (- c d) (* x (- c d))", environment));
		OperatorExpression oe2 = (OperatorExpression) (Expression.parse("- c d", environment));
		OperatorExpression oe3 = (OperatorExpression) (Expression.parse("/ x (+ y z)", environment));
		System.out.println(oe1);
		
		/*
		  map = { x1 -> x2;
		          y1 -> y2;
		          z1 -> z2 }
		  this = "= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"
		  returns the expression "= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"
		*/
		Expression e5 = Expression.parse("= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1", environment);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x1", "x2");
		map.put("y1", "y2");
		map.put("z1", "z2");
		Expression e6 = e5.substitute(map);
		System.out.println(e6);
		System.out.println(e6.matchesTemplate("= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"));
		printHashMapOfStrings(e5.findPairings(e6));
		Expression e7 = Expression.parse("= x (+ y z)", environment);
		Expression e8 = Expression.parse("= x (+ y z)", environment);
		System.out.println("expressions are equal is " + e7.equals(e8));
		
		BigDecimal one = BigDecimal.ONE;
		BigDecimal four = new BigDecimal("4");
		BigDecimal oneFourth = new BigDecimal("0.25");
		BigDecimal notherOne = four.multiply(oneFourth);
		System.out.println(one);
		System.out.println(notherOne);
		System.out.println(one.equals(notherOne));
		//VariableExpression ve = (VariableExpression) (Expression.parse("z"));
		//oe.applyLeft(new Operator("*"), ve);
		
		//testing latex in a JOptionPane...
		//String testLatexString = "\\underbrace{e^{i \\pi}}_{1}";
		//String newArrangementString = DialogHandler.showArrangementAndGroupingDialog(testLatexString);
		//System.out.println(newArrangementString);
	}

}
