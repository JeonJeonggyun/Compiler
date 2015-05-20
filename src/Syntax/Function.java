package Syntax;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract Syntax :
 * Syntax.Function = String name; Syntax.Expression*
 */
class Function extends Expression {
	protected final String name;
	protected final ArrayList<Expression> params;

	Function(String id, ArrayList<Expression> params) {
		name = id;
		this.params = params;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Syntax.Function " + name);

		for (Expression expression : params) {
			expression.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		if (valid) return;
		// todo

		valid = true;
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		// todo
		return null;
	}
}