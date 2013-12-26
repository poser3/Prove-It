import java.util.HashMap;


public class Tester {
	
	public void printHashMapOfStrings(HashMap<String, String> map) {
		for (String key: map.keySet()) {
			System.out.println(key + "->" + map.get(key) + ";");
		}
	}
	
	public Tester(MainWindow mainWindow) {
		System.out.println("Hi from the tester...");
		OperatorExpression oe1 = (OperatorExpression) (Expression.parse("= (+ a b c) (- c d) (* x (- c d))"));
		OperatorExpression oe2 = (OperatorExpression) (Expression.parse("- c d"));
		OperatorExpression oe3 = (OperatorExpression) (Expression.parse("/ x (+ y z)"));
		System.out.println(oe1);
		
		/*
		  map = { x1 -> x2;
		          y1 -> y2;
		          z1 -> z2 }
		  this = "= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"
		  returns the expression "= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"
		*/
		Expression e5 = (OperatorExpression) (Expression.parse("= (+ (* x1 (+ x1 y1)) (- y1 z1)) z1"));
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("x1", "x2");
		map.put("y1", "y2");
		map.put("z1", "z2");
		Expression e6 = e5.substitute(map);
		System.out.println(e6);
		System.out.println(e6.matchesTemplate("= (+ (* x2 (+ x2 y2)) (- y2 z2)) z2"));
		printHashMapOfStrings(e5.findPairings(e6));
		
		//VariableExpression ve = (VariableExpression) (Expression.parse("z"));
		//oe.applyLeft(new Operator("*"), ve);
	}

}

