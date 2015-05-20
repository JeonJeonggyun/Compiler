package Syntax;

import java.util.HashMap;

/**
 * Abstract Syntax :
 * Syntax.Return = Syntax.Expression?
 */
public class Return extends Statement {
	protected final Expression returnValue;

	public Return(Expression returnValue) {
		this.returnValue = returnValue;
	}

	public Return() {
		this(null);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Syntax.Return");
		if (returnValue != null) {
			returnValue.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}
