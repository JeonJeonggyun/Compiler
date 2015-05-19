/**
 * Created by 병훈 on 2015-05-09.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

abstract class AbstractSyntax {
	private boolean valid = false;
	protected final static HashMap<String, Init> globalVariableMap = new HashMap<>();
	protected final static HashSet<Global> globalFunctionMap = new HashSet<>();

	abstract void display(int k);

	abstract protected void V(HashMap<String, Init> declarationMap);

	protected final void check(boolean test, String msg) {
		if (test) return;
		System.err.println(msg);
		System.exit(1);
	}
}

/**
 * Abstract Syntax :
 * Program = Global*; Statements
 */
class Program extends AbstractSyntax {
	ArrayList<Global> globals;
	Statements statements;

	Program(ArrayList<Global> g, Statements s) {
		globals = g;
		statements = s;
	}

	private void mapGlobal() {
		for (Global global : globals) {

			if (global instanceof FunctionDeclaration) {
				FunctionDeclaration functionDeclaration = (FunctionDeclaration) global;
				functionDeclaration.mapParams();

				if (!globalFunctionMap.contains(global)) {
					globalFunctionMap.add(functionDeclaration);
				} else {
					check(false, "duplicated declaration " + functionDeclaration.name);
				}

			} else if (global instanceof Declaration) {
				for (Init init : ((Declaration) global).inits) {
					if (!globalVariableMap.containsKey(init.name)) {
						globalVariableMap.put(init.name, init);
					} else {
						check(false, "duplicated declaration " + init.name);
					}
				}

			} else {
				check(false, "never reach here");
			}
		}
	}

	@Override
	public void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Program");
		for (Global g : globals) {
			g.display(k + 1);
		}
		statements.display(k + 1);
	}


	public void V() {
		V(globalVariableMap);
	}


	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		mapGlobal();

		for (Global global : globals) {
			global.V(declarationMap);
		}

		if (statements != null) {
			statements.mapVariable();
			statements.V(declarationMap);
		}
	}
}


/**
 * Abstract Syntax :
 * Global = FunctionDeclaration | Declarations
 */
abstract class Global extends AbstractSyntax {
}


/**
 * Abstract Syntax :
 * FunctionDeclaration = Type; String id; ParamDeclaration*; Statements
 */
class FunctionDeclaration extends Global {
	Type type;
	String name;
	ArrayList<ParamDeclaration> params;
	Statements statements;
	private HashMap<String, Init> paramMap = new HashMap<>();

	FunctionDeclaration(Type t, String name, ArrayList<ParamDeclaration> p, Statements s) {
		type = t;
		this.name = name;
		params = p;
		statements = s;
	}

	protected void mapParams() {
		for (ParamDeclaration param : params) {
			if (!paramMap.containsKey(param.name)) {
				paramMap.put(param.name, param);
			} else {
				check(false, "duplicated declaration " + param.name);
			}
		}

		statements.mapVariable();
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("FunctionDeclaration " + type + " " + name);
		for (ParamDeclaration declaration : params) {
			declaration.display(k + 1);
		}
		statements.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
		HashMap<String, Init> localMap = new HashMap<>(declarationMap);
		int globalLength = localMap.size();
		int localLength = paramMap.size();
		localMap.putAll(paramMap);

		check(globalLength + localLength == localMap.size(), "duplicated declaration in function " + type + " " + name);
		
		statements.V(localMap);
	}
}


/**
 * Abstract Syntax :
 * ParamDeclaration = Type; String Id
 */
class ParamDeclaration extends Init {
	ParamDeclaration(Type t, String name) {
		type = t;
		this.name = name;
	}

	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("ParamDeclaration " + type + " " + name);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * Statements = Declaration*; Statement*
 */
class Statements extends AbstractSyntax {
	ArrayList<Declaration> declarations;
	ArrayList<Statement> statements;
	HashMap<String, Init> variableMap = new HashMap<>();

	Statements(ArrayList<Declaration> d, ArrayList<Statement> s) {
		declarations = d;
		statements = s;
	}

	protected void mapVariable() {
		for (Declaration declaration : declarations) {

			for (Init init : declaration.inits) {

				if (!variableMap.containsKey(init.name)) {
					variableMap.put(init.name, init);
				} else {
					check(false, "duplicated declaration " + init.name);
				}

			}

		}
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Statements");
		for (Declaration declaration : declarations) {
			declaration.display(k + 1);
		}
		for (Statement statement : statements) {
			statement.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Declaration = Init*
 */
class Declaration extends Global {
	ArrayList<Init> inits = new ArrayList<>();

	Declaration(ArrayList<Init> init) {
		this.inits = init;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println(this.getClass().getName());
		for (Init init : inits) {
			init.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Init = ArrayInit | NoArrayInit
 */
abstract class Init extends AbstractSyntax {
	Type type;
	String name;
}


/**
 * Abstract Syntax :
 * ArrayInit = Type; String id; int size; (Expression initList)*
 */
class ArrayInit extends Init {
	int size;
	ArrayList<Expression> initList;

	ArrayInit(Type t, String name, int s, ArrayList<Expression> i) {
		type = t;
		this.name = name;
		size = s;
		initList = i;
	}

	ArrayInit(Type t, String name, int s) {
		this(t, name, s, null);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("ArrayInit " + type + " " + name + "[" + size + "]");
		if (initList != null) {
			for (Expression expression : initList) {
				expression.display(k + 1);
			}
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * ArrayInit = Type; String id; (Expression initList)*
 */
class NoArrayInit extends Init {
	Expression initial;

	NoArrayInit(Type t, String name, Expression i) {
		type = t;
		this.name = name;
		initial = i;
	}

	NoArrayInit(Type t, String name) {
		this(t, name, null);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("NoArrayInit " + type + " " + name);

		if (initial != null) {
			initial.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Statement = Skip | IfStatement | Block | WhileStatement | SwitchStatement
 * | ForStatement | Return | Expression | Break | Continue
 */
abstract class Statement extends AbstractSyntax {
}


/**
 * Abstract Syntax :
 * IfStatement = Expression condition; Block; (Expression elseif; Block)*; Block?
 */
class IfStatement extends Statement {
	Expression condition;
	Block statements;
	ArrayList<IfStatement> elseIfs;
	Block elses;

	IfStatement(Expression c, Block s, ArrayList<IfStatement> elseIf, Block e) {
		condition = c;
		statements = s;
		elseIfs = elseIf;
		elses = e;
	}

	IfStatement(Expression c, Block s, ArrayList<IfStatement> elseIfs) {
		this(c, s, elseIfs, null);
	}

	IfStatement(Expression c, Block s, Block e) {
		this(c, s, null, e);
	}

	IfStatement(Expression c, Block s) {
		this(c, s, null, null);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("IfStatement");
		condition.display(k + 1);
		statements.display(k + 1);
		if (elseIfs != null) {
			for (IfStatement statement : elseIfs) {
				statement.display(k + 1);
			}
		}
		if (elses != null) {
			elses.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Block = Statement*
 */
class Block extends Statement {
	ArrayList<Statement> statements;

	Block(ArrayList<Statement> s) {
		statements = s;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Block");
		for (Statement statement : statements) {
			statement.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		for (Statement i : statements) {
			i.V(declarationMap);
		}
	}
}


/**
 * Abstract Syntax :
 * WhileStatement = Expression condition; Block
 */
class WhileStatement extends Statement {
	Expression condition;
	Block statements;

	WhileStatement(Expression c, Block s) {
		condition = c;
		statements = s;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("WhieStatement");
		condition.display(k + 1);
		statements.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		condition.V(declarationMap);
		Type testType = condition.typeOf(declarationMap);
		if (testType == Type.BOOL) {
			statements.V(declarationMap);
		} else {
			check(false, "poorly typed test in while Loop in Conditional: " + condition);
		}
	}
}


/**
 * Abstract Syntax :
 * SwitchStatement = Expression condition; (Literal case; Statement*)*; (Statement*)?
 */
class SwitchStatement extends Statement {
	Expression condition;
	HashMap<Value, ArrayList<Statement>> cases = new HashMap<>();
	ArrayList<Statement> defaults;

	SwitchStatement(Expression condition) {
		this.condition = condition;
	}

	void addCase(Value caseLiteral, ArrayList<Statement> statements) {
		cases.put(caseLiteral, statements);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("SwitchStatement");
		for (Value value : cases.keySet()) {
			value.display(k + 1);
			for (Statement statement : cases.get(value)) {
				statement.display(k + 1);
			}
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * ForStatement = Expression*; Expression; Expression*; Block
 */
class ForStatement extends Statement {
	ArrayList<Expression> preExpression = new ArrayList<>();
	ArrayList<Expression> postExpression = new ArrayList<>();
	Expression condition;
	Block statements;

	void addPreExpression(Expression expression) {
		preExpression.add(expression);
	}

	void addPostExpression(Expression expression) {
		postExpression.add(expression);
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("ForStatement");

		for (Expression expression : preExpression) {
			expression.display(k + 1);
		}
		condition.display(k + 1);
		for (Expression expression : postExpression) {
			expression.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Return = Expression?
 */
class Return extends Statement {
	Expression returnValue;

	Return(Expression returnValue) {
		this.returnValue = returnValue;
	}

	Return() {
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Return");
		if (returnValue != null) {
			returnValue.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Break =
 */
class Break extends Statement {
	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Break");
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Continue =
 */
class Continue extends Statement {
	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Continue");
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}
}


/**
 * Abstract Syntax :
 * Skip =
 */
class Skip extends Statement {
	@Override
	void display(int k) {

	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * Expression = VariableRef | Value | Binary | Unary | Function
 */
abstract class Expression extends Statement {
	abstract Type typeOf(HashMap<String, Init> declarationMap);
}


/**
 * Abstract Syntax :
 * VariableRef = Variable | ArrayRef
 */
abstract class VariableRef extends Expression {
}


/**
 * Abstract Syntax :
 * Variable = String id
 */
class Variable extends VariableRef {
	String name;

	Variable(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			String s = ((Variable) obj).name;
			return name.equals(s); // case-sensitive identifiers
		} else return false;
	}

	public int hashCode() {
		return name.hashCode();
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Variable " + name);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		check(declarationMap.containsKey(this.name), "undeclared variable: " + this.name);
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		check(declarationMap.containsKey(this.name), "undefined variable: " + this.name);
		return declarationMap.get(this.name).type;
	}
}


/**
 * Abstract Syntax :
 * ArrayRef = String id; Expression index
 */
class ArrayRef extends VariableRef {
	String name;
	Expression index;

	ArrayRef(String n, Expression index) {
		this.name = n;
		this.index = index;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("ArrayRef " + name);
		index.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		check(declarationMap.containsKey(this.name), "undeclared variable: " + this.name); // index처리?
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		check(declarationMap.containsKey(this.name), "undefined variable: " + this.name);
		return declarationMap.get(this.name).type;
	}
}


/**
 * Abstract Syntax :
 * Value = IntValue | BoolValue | FloatValue | CharValue | TimeValue | DateValue
 */
abstract class Value extends Expression {
	Type type;

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		return type;
	}
}


/**
 * Abstract Syntax :
 * Binary = Operator; Expression e1, e2
 */
class Binary extends Expression {
	Operator op;
	Expression term1, term2;

	Binary(Operator o, Expression l, Expression r) {
		op = o;
		term1 = l;
		term2 = r;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Binary");

		op.display(k + 1);
		term1.display(k + 1);
		term2.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		Type typ1 = term1.typeOf(declarationMap);
		Type typ2 = term2.typeOf(declarationMap);
		term1.V(declarationMap);
		term2.V(declarationMap);
		if (op.ArithmeticOp()) {
			check(typ1 == typ2 &&
					(typ1 == Type.INT || typ1 == Type.FLOAT)
					, "type error for " + op);
		} else if (op.RelationalOp())
			check(typ1 == typ2, "type error for " + op);
		else if (op.BooleanOp())
			check(typ1 == Type.BOOL && typ2 == Type.BOOL, op + ": non-bool operand");
		else
			throw new IllegalArgumentException("should never reach here BinaryOp error");
	}


	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		if (op.ArithmeticOp())
			if (term1.typeOf(declarationMap) == Type.FLOAT)
				return (Type.FLOAT);
			else return (Type.INT);
		if (op.RelationalOp() || op.BooleanOp())
			return (Type.BOOL);
		else
			return null;
	}
}


/**
 * Abstract Syntax :
 * Unary = UnaryOperator; Expression
 */
class Unary extends Expression {
	Operator op;
	Expression term;

	Unary(Operator o, Expression e) {
		op = o;
		term = e;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Unary");

		op.display(k + 1);
		term.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		Type type = term.typeOf(declarationMap); //start here
		term.V(declarationMap);
		if (op.NotOp()) {
			check((type == Type.BOOL), "type error for NotOp " + op);
		} else if (op.NegateOp()) {
			check((type == (Type.INT) || type == (Type.FLOAT)), "type error for NegateOp " + op);
		} else {
			throw new IllegalArgumentException("should never reach here UnaryOp error");
		}
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		if (op.NotOp()) return (Type.BOOL);
		else if (op.NegateOp()) return term.typeOf(declarationMap);
		else if (op.intOp()) return (Type.INT);
		else if (op.floatOp()) return (Type.FLOAT);
		else if (op.charOp()) return (Type.CHAR);
		else return null;
	}
}


/**
 * Abstract Syntax :
 * Function = String name; Expression*
 */
class Function extends Expression {
	String name;
	ArrayList<Expression> params;

	Function(String id, ArrayList<Expression> params) {
		name = id;
		this.params = params;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Function " + name);

		for (Expression expression : params) {
			expression.display(k + 1);
		}
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		// todo
		return null;
	}
}


/**
 * Abstract Syntax :
 * TypeCast = Type type; Expression
 */
class TypeCast extends Expression {
	Type type;
	Expression expression;

	TypeCast(Type t, Expression e) {
		type = t;
		expression = e;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("TypeCast " + type);
		expression.display(k + 1);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
	}

	@Override
	Type typeOf(HashMap<String, Init> declarationMap) {
		// todo
		return null;
	}
}

/**
 * Abstract Syntax :
 * Type = 'int' | 'bool' | 'void' | 'char' | 'float' | 'time' | 'date'
 */
class Type extends AbstractSyntax {
	// Type = int | bool | char | float
	final static Type INT = new Type("int");
	final static Type BOOL = new Type("bool");
	final static Type CHAR = new Type("char");
	final static Type FLOAT = new Type("float");
	final static Type VOID = new Type("void");
	final static Type TIME = new Type("time");
	final static Type DATE = new Type("date");

	private String value;

	private Type(String t) {
		value = t;
	}

	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Type) {
			Type tmp = (Type) obj;
			return tmp.value.equals(this.value);
		} else return false;
	}

	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Type " + value);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * IntValue = int
 */
class IntValue extends Value {
	private int value = 0;

	IntValue(int v) {
		type = Type.INT;
		value = v;
	}

	int intValue() {
		return value;
	}

	public String toString() {
		return "" + value;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("IntValue " + value);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * BoolValue = bool
 */
class BoolValue extends Value {
	private boolean value = false;

	BoolValue(boolean v) {
		type = Type.BOOL;
		value = v;
	}

	boolean boolValue() {
		return value;
	}

	int intValue() {
		return value ? 1 : 0;
	}

	public String toString() {
		return "" + value;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("BoolValue " + value);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * CharValue = String
 */
class CharValue extends Value {
	private char value = ' ';

	CharValue(char v) {
		type = Type.CHAR;
		value = v;
	}

	char charValue() {
		return value;
	}

	public String toString() {
		return "" + value;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("CharValue " + value);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * FloatValue = float
 */
class FloatValue extends Value {
	private float value = 0;

	FloatValue(float v) {
		type = Type.FLOAT;
		value = v;
	}

	float floatValue() {
		return value;
	}

	public String toString() {
		return "" + value;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("FloatValue " + value);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * DateValue = String year, month, day
 */
class DateValue extends Value {
	private int year, month, day;

	DateValue(String y, String m, String d) {
		type = Type.DATE;
		year = Integer.parseInt(y);
		month = Integer.parseInt(m);
		day = Integer.parseInt(d);
	}

	public String toString() {
		return "" + year + "/" + month + "/" + day;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("DateValue " + year + "." + month + "." + day);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * TimeValue = String hour, minute, second
 */
class TimeValue extends Value {
	private int hour, minute, second;

	TimeValue(String h, String m, String s) {
		type = Type.TIME;
		hour = Integer.parseInt(h);
		minute = Integer.parseInt(m);
		second = Integer.parseInt(s);
	}

	public String toString() {
		return "" + hour + "/" + minute + "/" + second;
	}

	@Override
	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("TimeValue " + hour + ":" + minute + ":" + second);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
	}
}


/**
 * Abstract Syntax :
 * Operator = BooleanOp | RelationalOp | ArithmeticOp | UnaryOp | AssignOp
 */
class Operator {
	// BooleanOp = && | ||
	final static String AND = "&&";
	final static String OR = "||";
	// RelationalOp = < | <= | == | != | >= | >
	final static String LT = "<";
	final static String LE = "<=";
	final static String EQ = "==";
	final static String NE = "!=";
	final static String GT = ">";
	final static String GE = ">=";
	// ArithmeticOp = + | - | * | /
	final static String PLUS = "+";
	final static String MINUS = "-";
	final static String TIMES = "*";
	final static String DIV = "/";
	final static String MOD = "%";
	// UnaryOp = !
	final static String NOT = "!";
	final static String NEG = "-";
	// CastOp = int | float | char
	final static String INT = "int";
	final static String FLOAT = "float";
	final static String CHAR = "char";
	// AssignOp
	final static String PLUSASSIGN = "+=";
	final static String MINUSASSIGN = "-=";
	final static String TIMESASSIGN = "*=";
	final static String DIVASSIGN = "/=";
	final static String MODASSIGN = "%=";
	// DoubleOp
	final static String PLUSPLUS = "++";
	final static String MINUSMINUS = "--";

	// Typed Operators
	// RelationalOp = < | <= | == | != | >= | >
	final static String INT_LT = "INT<";
	final static String INT_LE = "INT<=";
	final static String INT_EQ = "INT==";
	final static String INT_NE = "INT!=";
	final static String INT_GT = "INT>";
	final static String INT_GE = "INT>=";
	// ArithmeticOp = + | - | * | / | % | ++ | --
	final static String INT_PLUS = "INT+";
	final static String INT_MINUS = "INT-";
	final static String INT_TIMES = "INT*";
	final static String INT_DIV = "INT/";
	final static String INT_MOD = "INT%";
	final static String INT_PP = "INT++";
	final static String INT_MM = "INT--";
	// UnaryOp = -
	final static String INT_NEG = "-";
	// AssignOp = += | -= | *= | /= | %=
	final static String INT_PLUS_ASSIGN = "INT+=";
	final static String INT_MINUS_ASSIGN = "INT-=";
	final static String INT_TIMES_ASSIGN = "INT*=";
	final static String INT_DIV_ASSIGN = "INT/=";
	final static String INT_MOD_ASSIGN = "INT%=";

	// RelationalOp = < | <= | == | != | >= | > | 
	final static String FLOAT_LT = "FLOAT<";
	final static String FLOAT_LE = "FLOAT<=";
	final static String FLOAT_EQ = "FLOAT==";
	final static String FLOAT_NE = "FLOAT!=";
	final static String FLOAT_GT = "FLOAT>";
	final static String FLOAT_GE = "FLOAT>=";
	// ArithmeticOp = + | - | * | / | % | ++ | -
	final static String FLOAT_PLUS = "FLOAT+";
	final static String FLOAT_MINUS = "FLOAT-";
	final static String FLOAT_TIMES = "FLOAT*";
	final static String FLOAT_DIV = "FLOAT/";
	final static String FLOAT_MOD = "FLOAT%";
	final static String FLOAT_PP = "FLOAT++";
	final static String FLOAT_MM = "FLOAT--";
	// UnaryOp = -
	final static String FLOAT_NEG = "-";
	// AssignOp = += | -= | *= | /= | %=
	final static String FLOAT_PLUS_ASSIGN = "FLOAT+=";
	final static String FLOAT_MINUS_ASSIGN = "FLOAT-=";
	final static String FLOAT_TIMES_ASSIGN = "FLOAT*=";
	final static String FLOAT_DIV_ASSIGN = "FLOAT/=";
	final static String FLOAT_MOD_ASSIGN = "FLOAT%=";

	// RelationalOp = < | <= | == | != | >= | > | 
	final static String TIME_LT = "TIME<";
	final static String TIME_LE = "TIME<=";
	final static String TIME_EQ = "TIME==";
	final static String TIME_NE = "TIME!=";
	final static String TIME_GT = "TIME>";
	final static String TIME_GE = "TIME>=";
	// ArithmeticOp = + | - | * | / | % | ++ | -
	final static String TIME_PLUS = "TIME+";
	final static String TIME_MINUS = "TIME-";
	final static String TIME_TIMES = "TIME*";
	final static String TIME_DIV = "TIME/";
	final static String TIME_MOD = "TIME%";
	final static String TIME_PP = "TIME++";
	final static String TIME_MM = "TIME--";
	// AssignOp = += | -= | *= | /= | %=
	final static String TIME_PLUS_ASSIGN = "TIME+=";
	final static String TIME_MINUS_ASSIGN = "TIME-=";
	final static String TIME_TIMES_ASSIGN = "TIME*=";
	final static String TIME_DIV_ASSIGN = "TIME/=";
	final static String TIME_MOD_ASSIGN = "TIME%=";

	// RelationalOp = < | <= | == | != | >= | > | 
	final static String DATE_LT = "DATE<";
	final static String DATE_LE = "DATE<=";
	final static String DATE_EQ = "DATE==";
	final static String DATE_NE = "DATE!=";
	final static String DATE_GT = "DATE>";
	final static String DATE_GE = "DATE>=";
	// ArithmeticOp = + | - | * | / | % | ++ | -
	final static String DATE_PLUS = "DATE+";
	final static String DATE_MINUS = "DATE-";
	final static String DATE_TIMES = "DATE*";
	final static String DATE_DIV = "DATE/";
	final static String DATE_MOD = "DATE%";
	final static String DATE_PP = "DATE++";
	final static String DATE_MM = "DATE--";
	// AssignOp = += | -= | *= | /= | %=
	final static String DATE_PLUS_ASSIGN = "DATE+=";
	final static String DATE_MINUS_ASSIGN = "DATE-=";
	final static String DATE_TIMES_ASSIGN = "DATE*=";
	final static String DATE_DIV_ASSIGN = "DATE/=";
	final static String DATE_MOD_ASSIGN = "DATE%=";

	// RelationalOp = < | <= | == | != | >= | >
	final static String CHAR_LT = "CHAR<";
	final static String CHAR_LE = "CHAR<=";
	final static String CHAR_EQ = "CHAR==";
	final static String CHAR_NE = "CHAR!=";
	final static String CHAR_GT = "CHAR>";
	final static String CHAR_GE = "CHAR>=";

	// RelationalOp = < | <= | == | != | >= | >
	final static String BOOL_LT = "BOOL<";
	final static String BOOL_LE = "BOOL<=";
	final static String BOOL_EQ = "BOOL==";
	final static String BOOL_NE = "BOOL!=";
	final static String BOOL_GT = "BOOL>";
	final static String BOOL_GE = "BOOL>=";
	// UnaryOp = !
	final static String BOOL_COMP = "!";

	// Type specific cast
	final static String I2F = "I2F";
	final static String F2I = "F2I";
	final static String C2I = "C2I";
	final static String I2C = "I2C";

	String value;

	Operator(String s) {
		value = s;
	}

	public String toString() {
		return value;
	}

	public boolean equals(Object obj) {
		return value.equals(obj);
	}

	boolean BooleanOp() {
		return value.equals(AND) || value.equals(OR);
	}

	boolean RelationalOp() {
		return value.equals(LT) || value.equals(LE) || value.equals(EQ)
				|| value.equals(NE) || value.equals(GT) || value.equals(GE);
	}

	boolean ArithmeticOp() {
		return value.equals(PLUS) || value.equals(MINUS) || value.equals(TIMES)
				|| value.equals(DIV) || value.equals(MOD);
	}

	boolean AssignOP() {
		return value.equals(PLUSASSIGN) || value.equals(MINUSASSIGN) || value.equals(TIMESASSIGN)
				|| value.equals(MODASSIGN) || value.equals(DIVASSIGN);
	}

	boolean NotOp() {
		return value.equals(NOT);
	}

	boolean NegateOp() {
		return value.equals(NEG);
	}

	boolean intOp() {
		return value.equals(INT);
	}

	boolean floatOp() {
		return value.equals(FLOAT);
	}

	boolean charOp() {
		return value.equals(CHAR);
	}

	final static String intMap[][] = {
			{PLUS, INT_PLUS}, {MINUS, INT_MINUS}, {TIMES, INT_TIMES}, {DIV, INT_DIV}, {MOD, INT_MOD},
			{PLUSPLUS, INT_PP}, {MINUSMINUS, INT_MM},
			{PLUSASSIGN, INT_PLUS_ASSIGN}, {MINUSASSIGN, INT_MINUS_ASSIGN}, {TIMESASSIGN, INT_TIMES_ASSIGN},
			{DIVASSIGN, INT_DIV_ASSIGN}, {MODASSIGN, INT_MOD_ASSIGN},
			{EQ, INT_EQ}, {NE, INT_NE}, {LT, INT_LT}, {LE, INT_LE}, {GT, INT_GT}, {GE, INT_GE},
			{NEG, INT_NEG}, {FLOAT, I2F}, {CHAR, I2C}
	};

	final static String floatMap[][] = {
			{PLUS, FLOAT_PLUS}, {MINUS, FLOAT_MINUS}, {TIMES, FLOAT_TIMES}, {DIV, FLOAT_DIV}, {MOD, FLOAT_MOD},
			{PLUSPLUS, FLOAT_PP}, {MINUSMINUS, FLOAT_MM},
			{PLUSASSIGN, FLOAT_PLUS_ASSIGN}, {MINUSASSIGN, FLOAT_MINUS_ASSIGN}, {TIMESASSIGN, FLOAT_TIMES_ASSIGN},
			{DIVASSIGN, FLOAT_DIV_ASSIGN}, {MODASSIGN, FLOAT_MOD_ASSIGN},
			{EQ, FLOAT_EQ}, {NE, FLOAT_NE}, {LT, FLOAT_LT}, {LE, FLOAT_LE}, {GT, FLOAT_GT}, {GE, FLOAT_GE},
			{NEG, FLOAT_NEG}, {INT, F2I}
	};

	final static String charMap[][] = {
			{EQ, CHAR_EQ}, {NE, CHAR_NE}, {LT, CHAR_LT}, {LE, CHAR_LE}, {GT, CHAR_GT}, {GE, CHAR_GE},
			{INT, C2I}
	};

	final static String boolMap[][] = {
			{EQ, BOOL_EQ}, {NE, BOOL_NE}, {LT, BOOL_LT}, {LE, BOOL_LE}, {GT, BOOL_GT}, {GE, BOOL_GE},
			{NOT, BOOL_COMP}
	};

	final static String timeMap[][] = {
			{PLUS, TIME_PLUS}, {MINUS, TIME_MINUS}, {TIMES, TIME_TIMES}, {DIV, TIME_DIV}, {MOD, TIME_MOD},
			{PLUSPLUS, TIME_PP}, {MINUSMINUS, TIME_MM},
			{PLUSASSIGN, TIME_PLUS_ASSIGN}, {MINUSASSIGN, TIME_MINUS_ASSIGN}, {TIMESASSIGN, TIME_TIMES_ASSIGN},
			{DIVASSIGN, TIME_DIV_ASSIGN}, {MODASSIGN, TIME_MOD_ASSIGN},
			{EQ, TIME_EQ}, {NE, TIME_NE}, {LT, TIME_LT}, {LE, TIME_LE}, {GT, TIME_GT}, {GE, TIME_GE}
	};

	final static String dateMap[][] = {
			{PLUS, DATE_PLUS}, {MINUS, DATE_MINUS}, {TIMES, DATE_TIMES}, {DIV, DATE_DIV}, {MOD, DATE_MOD},
			{PLUSPLUS, DATE_PP}, {MINUSMINUS, DATE_MM},
			{PLUSASSIGN, DATE_PLUS_ASSIGN}, {MINUSASSIGN, DATE_MINUS_ASSIGN}, {TIMESASSIGN, DATE_TIMES_ASSIGN},
			{DIVASSIGN, DATE_DIV_ASSIGN}, {MODASSIGN, DATE_MOD_ASSIGN},
			{EQ, DATE_EQ}, {NE, DATE_NE}, {LT, DATE_LT}, {LE, DATE_LE}, {GT, DATE_GT}, {GE, DATE_GE}
	};

	static private Operator map(String[][] tmap, String op) {
		for (String[] aTmap : tmap) {
			if (aTmap[0].equals(op)) {
				return new Operator(aTmap[1]);
			}
		}
		assert false : "should never reach here";
		return null;
	}

	static public Operator intMap(String op) {
		return map(intMap, op);
	}

	static public Operator floatMap(String op) {
		return map(floatMap, op);
	}

	static public Operator charMap(String op) {
		return map(charMap, op);
	}

	static public Operator boolMap(String op) {
		return map(boolMap, op);
	}

	static public Operator timeMap(String op) {
		return map(timeMap, op);
	}

	static public Operator dateMap(String op) {
		return map(dateMap, op);
	}

	void display(int k) {
		for (int w = 0; w < k; w++) {
			System.out.print("\t");
		}

		System.out.println("Operator " + value);
	}

}
