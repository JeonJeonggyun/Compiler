package Syntax;

import CodeGenerator.CodeGenerator;

import java.util.HashMap;

/**
 * date 값을 나타내는 구문
 * <p>
 * Abstract Syntax :
 * DateValue = String year, month, day
 */
public class DateValue extends Value {
	/**
	 * 년, 월, 일을 나타내는 변수
	 */
	protected final int year, month, day;

	public DateValue(String y, String m, String d) {
		type = Type.DATE;
		year = Integer.parseInt(y);
		month = Integer.parseInt(m);
		day = Integer.parseInt(d);
	}

	public String toString() {
		return "" + year + "/" + month + "/" + day;
	}

	@Override
	void display(int lev) {
		for (int i = 0; i < lev; i++) {
			System.out.print("\t");
		}

		System.out.println("DateValue " + year + "." + month + "." + day);
	}

	@Override
	protected void V(HashMap<String, Init> declarationMap) {
		// todo
		if (valid) return;

		check(year >= 0, "year can not be negative value");
		check(month >= 1 && month <= 12, "month value must be between 1 and 12");
		check(month >= 1 && month <= 12, "month value must be between 1 and 12");

		valid = true;
	}

	@Override
	public void genCode() {
		CodeGenerator.ldc(year * 10000 + month * 100 + day);
	}
}
