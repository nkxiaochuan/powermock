package org.powermock.reflect;

import java.util.HashMap;
import java.util.Map;

public class Dummy {
	private Map<String, Object> map = new HashMap<String, Object>();
	private SomethingOther something;
	
	private Object get(String name) {
		return map.get(name);
	}

	private void put(String name, Object v) {
		map.put(name, v);
	}
}
