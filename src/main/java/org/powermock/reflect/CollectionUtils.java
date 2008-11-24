package org.powermock.reflect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionUtils {

	public static <T> List<T> filter(List<T> list, Predicate<T> p) {
		List<T> result = new ArrayList<T>();
		for (Iterator<T> i = list.iterator(); i.hasNext();) {
			T obj = i.next();
			if (p.evaluate(obj)) {
				result.add(obj);
			}
		}
		return result;
	}

}
