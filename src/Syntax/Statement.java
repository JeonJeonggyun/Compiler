package Syntax;

import java.util.HashMap;

import Syntax.AbstractSyntax;

/**
 * Abstract Syntax :
 * Syntax.Statement = Syntax.Skip | Syntax.IfStatement | Syntax.Block | Syntax.WhileStatement | Syntax.SwitchStatement
 * | Syntax.ForStatement | Syntax.Return | Syntax.Expression | Syntax.Break | Syntax.Continue
 */
abstract public class Statement extends AbstractSyntax {	
	abstract void V(HashMap<String, Init> declarationMap, Statement s);    // validation

	/**
	 * <tt>test</tt>�� Ȯ���ؼ� false�� <tt>msg</tt>�� ����ϰ� ������
	 *
	 * @param test Ȯ�� �ϰ� ���� ���̳� ����
	 * @param msg  <tt>test</tt>�� �������� �� �� ��� ��� �� �޽���
	 */

}

