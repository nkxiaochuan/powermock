package org.powermock.reflect;

import static junit.framework.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.powermock.reflect.PowerReflect.Method1;

public class PowerReflectTest {

	@Test
	public void testSyntax() {
		Dummy dummy = new Dummy();
		Map map = PowerReflect.fieldOfType(Map.class).getFrom(dummy);
		map.put("qwe", "asd");

		// test invocation using forObject
		assertEquals("asd", PowerReflect.forObject(dummy).methodReturning(Object.class).havingArguments(String.class).invoke("qwe"));
		PowerReflect.forObject(dummy).voidMethod().havingArguments(String.class, Object.class).invoke("a", "b");
		assertEquals("b", PowerReflect.forObject(dummy).methodReturning(Object.class).havingArguments(String.class).invoke("a"));
		
		// test invocation using forClass
		Method1<Dummy, Object, String> methodInvoker = PowerReflect.forClass(Dummy.class).methodReturning(Object.class).havingArguments(String.class);
		assertEquals("b", methodInvoker.on(dummy).invoke("a"));
		
		// TODO: is forClass really necessary?
	}
}
