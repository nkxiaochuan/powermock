package org.powermock.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ArgumentTypePredicate implements Predicate<Method> {

	private Class[] argumentTypes;

	public ArgumentTypePredicate(Class... argumentTypes) {
		this.argumentTypes = argumentTypes;
	}

	public boolean evaluate(Method method) {
		return Arrays.equals(argumentTypes, method.getParameterTypes());
	}

}
