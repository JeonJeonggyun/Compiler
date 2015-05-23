package Semantic;

import Syntax.Type;

import java.util.ArrayList;


/**
 * �Լ� ������ ��� <tt>FunctionInformation</tt>�� ��ü���� container�̴�.
 * Type checking�� �Լ��� �ùٸ��� ���� �Ǿ����� Ȯ���� ���� ���δ�.
 *
 * @see Syntax.Program
 */
public class FunctionSet {
	/**
	 * �Լ������� <tt>FunctionInformation</tt>�� ����ִ� <tt>ArrayList</tt>
	 */
	private ArrayList<FunctionInformation> array = new ArrayList<>();

	/**
	 * ������ <tt>infoToCheck</tt>�� �� set�� �ִ��� ���θ� ��ȯ.
	 *
	 * @param infoToCheck ���� ���θ� Ȯ���� <tt>FunctionInformation</tt>��ü
	 * @return set�� ���� �Ǿ��ִ��� ����
	 */
	public boolean contains(FunctionInformation infoToCheck) {
		for (FunctionInformation information : array) {
			if (information.equals(infoToCheck)) return true;
		}

		return false;
	}

	/**
	 * set�� <tt>functionInformation</tt>��ü�� �߰�.
	 * <p>
	 * ���� �̹� �ִٸ� �߰����� ���� (set�� Ư¡).
	 *
	 * @param functionInformation set�� �߰��� <tt>FunctionInformation</tt>��ü
	 */
	public void add(FunctionInformation functionInformation) {
		if (contains(functionInformation)) return;

		array.add(functionInformation);
	}

	/**
	 * ã�� �Լ��� �̸��� ����, �Ű������� ������ ��ġ�ϴ� �Լ��� Type�� ��ȯ�Ѵ�.
	 *
	 * @param name      ã�� �Լ��� �̸�
	 * @param paramType ã�� �Լ��� �Ű����� ����
	 * @return ã�� �Լ��� ��ȯ��
	 */
	public Type getFunctionType(String name, ArrayList<Type> paramType) {
		for (FunctionInformation function : array) {
			if (function.name.equals(name) && function.paramType.equals(paramType)) {
				return function.type;
			}
		}

		System.err.println("Compiler error in FunctionSet. can not find such function");
		System.exit(1);
		return null;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("");

		for (FunctionInformation information : array) {
			result.append(information.toString()).append("\n");
		}

		return result.toString();
	}
}
