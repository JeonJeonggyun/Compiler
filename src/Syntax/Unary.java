package Syntax;

import java.util.HashMap;

/**
 * Abstract Syntax :
 * Syntax.Unary = UnaryOperator; Syntax.Expression
 */
public class Unary extends Expression {
	protected final Operator op;
	protected final Expression term;

	public Unary(Operator o, Expression e) {
		op = o;
		term = e;
	}

	@Override
	void display(int lev) {
		for (int i = 0; i < lev; i++) {
			System.out.print("\t");
		}

		System.out.println("Unary");

		op.display(lev + 1);
		term.display(lev + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		if (valid) return;

		Type type = term.typeOf(declarationMap); //start here
		term.V(declarationMap);
		if (op.NotOp()) {
			check((type == Type.BOOL), "type error for NotOp " + op);

		} else if (op.NegateOp()) {
			check((type == (Type.INT) || type == (Type.FLOAT)),
					"type error for NegateOp " + op);

		} else if (op.incOp()) {
			check(type != Type.BOOL || type != Type.VOID,
					"type error for increase or decrease Op");
		} else {
			throw new IllegalArgumentException("should never reach here UnaryOp error");
		}

		valid = true;
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		// todo 수정
		if (op.NotOp()) return (Type.BOOL);
		else if (op.NegateOp()) return term.typeOf(declarationMap);
		else if (op.intOp()) return (Type.INT);
		else return null;
	}
}