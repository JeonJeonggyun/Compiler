package Semantic;

import Syntax.FunctionDeclaration;
import Syntax.ParamDeclaration;
import Syntax.Type;

import java.util.ArrayList;

/**
 * �Լ� �ϳ��� ������ ����(�Լ��� ��ȯ Ÿ��, �̸�, �Ķ���͵��� Ÿ��)�� ������.
 * <p>
 * �Լ� ���� Ÿ�缺 Ȯ���� ���� ����.
 *
 * @see	Parser.Parser
 * @see FunctionSet
 */
public class FunctionInformation {
	/**
	 * �Լ��� ��ȯ Ÿ��
	 */
	protected Type type;
	/**
	 * �Լ��� �̸�
	 */
	protected String name;
	/**
	 * �Ķ���͵��� Ÿ���� <tt>Type</tt>������ ����
	 */
	protected ArrayList<Type> paramType;

	public FunctionInformation(FunctionDeclaration declaration) {
		type = declaration.getType();
		name = declaration.getName();
		paramType = new ArrayList<>();

		for (ParamDeclaration param : declaration.getParams()) {
			paramType.add(param.getType());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FunctionInformation)) return false;

		FunctionInformation tmp = (FunctionInformation) obj;

		return name.equals(tmp.name) && paramType.equals(tmp.paramType);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("");

		result.append(type).append(" ").append(name).append(" ");
		for (Type type : paramType) {
			result.append(type).append(" ");
		}

		return result.toString();
	}
}
