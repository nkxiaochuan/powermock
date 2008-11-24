package org.powermock.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class PowerReflect {

	public static <T> FieldQuery<T> fieldOfType(final Class<T> clazz) {
		return new FieldQuery<T>() {
			public T getFrom(Object o) {
				Field[] fields = o.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getType().equals(clazz)) {
						field.setAccessible(true);
						try {
							return (T) field.get(o);
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
				throw new IllegalArgumentException("Field not found");
			}
		};
	}

	public static <T> MethodQuery<T> forObject(T object) {
		return new MethodQueryImpl<T>(new Target<T>(object, Arrays.asList(object.getClass().getDeclaredMethods())));
	}

	public static <T> MethodQuery<T> forClass(Class<T> clazz) {
		return new MethodQueryImpl<T>(new Target<T>(null, Arrays.asList(clazz.getDeclaredMethods())));
	}

	static interface MethodQuery<T> {
		<R> ExtendedMethodQuery<T, R> methodReturning(Class<R> class1);
		<R> ExtendedMethodQuery<T, R> voidMethod();
	}

	static interface ExtendedMethodQuery<T, R> {
		<A1> Method1<T, R, A1> havingArguments(Class<A1> a1);
		<A1, A2> Method2<T, R, A1, A2> havingArguments(Class<A1> a1, Class<A2> a2);
	}
	
	static class Target<T> {
		private T target;
		private List<Method> methods;
		public Target(T target, List<Method> methods) {
			this.target = target;
			this.methods = methods;
		}
		
		public Target<T> filter(Predicate<Method> p) {
			return new Target<T>(target, CollectionUtils.filter(methods, p));
		}

		public Target<T> setTarget(T target) {
			return new Target<T>(target, methods);
		}

		public Object invoke(Object[] arguments) {
			try {
				Method method = methods.get(0);
				method.setAccessible(true);
				return method.invoke(target, arguments);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class MethodQueryImpl<T> implements MethodQuery<T> {
		private Target<T> target;

		public MethodQueryImpl(Target<T> target) {
			this.target = target;
		}

		public <R> ExtendedMethodQuery<T, R> methodReturning(Class<R> r) {
			return new ExtendedMethodQueryImpl<T, R>(target.filter(new ReturnValuePredicate(r)));
		}

		public <R> ExtendedMethodQuery<T, R> voidMethod() {
			return new ExtendedMethodQueryImpl<T, R>(target.filter(new ReturnValuePredicate(Void.TYPE)));
		}
	}

	static class ExtendedMethodQueryImpl<T, R> implements ExtendedMethodQuery<T, R> {

		private Target<T> target;

		public ExtendedMethodQueryImpl(Target<T> target) {
			this.target = target;
		}

		public <A1> Method1<T, R, A1> havingArguments(Class<A1> a1) {
			Class<?>[] classes = new Class[] { a1 };
			return new Method1<T, R, A1>(target.filter(new ArgumentTypePredicate(classes)));
		}
		
		public <A1, A2> Method2<T, R, A1, A2> havingArguments(Class<A1> a1, Class<A2> a2) {
			Class<?>[] classes = new Class[] { a1, a2 };
			return new Method2<T, R, A1, A2>(target.filter(new ArgumentTypePredicate(classes)));
		}
		
	}

	static class Method1<T, R, A1> {
		private Target<T> target;

		public Method1(Target<T> target) {
			this.target = target;
		}

		@SuppressWarnings("unchecked")
		public R invoke(A1 a1) {
			return (R) target.invoke(new Object[] { a1 });
		}

		public Method1<T, R, A1> on(T target) {
			return new Method1<T, R, A1>(this.target.setTarget(target));
		}
	}

	static class Method2<T, R, A1, A2> {
		private Target<T> target;

		public Method2(Target<T> target) {
			this.target = target;
		}

		@SuppressWarnings("unchecked")
		public R invoke(A1 a1, A2 a2) {
			return (R) target.invoke(new Object[] { a1, a2 });
		}
	}
}

