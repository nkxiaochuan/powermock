package org.powermock.reflect;

import java.lang.reflect.Method;

public class ReturnValuePredicate implements Predicate<Method> {

	private Class<?> clazz;

	public ReturnValuePredicate(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean evaluate(Method m) {
		return clazz.equals(m.getReturnType());
	}

}
