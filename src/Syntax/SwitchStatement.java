package Syntax;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract Syntax :
 * Syntax.SwitchStatement = Syntax.Expression condition; (Literal case; Syntax.Statement*)*; (Syntax.Statement*)?
 */
public class SwitchStatement extends Statement {
	protected Type switchType;
	protected final Expression condition;
	protected final HashMap<Value, ArrayList<Statement>> cases = new HashMap<>();
	protected ArrayList<Statement> defaults = null;

	SwitchStatement(Expression condition) {
		this.condition = condition;
	}

	void addCase(Value caseLiteral, ArrayList<Statement> statements) {
		check(cases.containsKey(caseLiteral),
				"duplicated case literal in switch");

		cases.put(caseLiteral, statements);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Syntax.SwitchStatement");
		for (Value value : cases.keySet()) {
			value.display(k + 1);
			for (Statement statement : cases.get(value)) {
				statement.display(k + 1);
			}
		}
	}

	@Override
	public void V(HashMap<String, Init> declarationMap) {
		// todo 확인
		if (valid) return;

		switchType = condition.typeOf(declarationMap);

		for (Value key : cases.keySet()) {
			check(key.typeOf(declarationMap) != switchType,
					"different type of case literal in switch. case : " + key.typeOf(declarationMap));

			for (Statement statement : cases.get(key)) {
				statement.V(declarationMap);
			}
		}

		if (defaults != null) {
			for (Statement statement : defaults) {
				statement.V(declarationMap);
			}
		}

		valid = true;
	}
}