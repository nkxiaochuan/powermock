/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.invocationcontrol.method.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.verification.api.VerificationMode;
import org.powermock.core.WhiteboxImpl;
import org.powermock.core.invocationcontrol.method.MethodInvocationControl;

/**
 * The default implementation of the {@link MethodInvocationControl} interface.
 * 
 * @author Johan Haleby
 */
public class MethodInvocationControlImpl<T> implements
		MethodInvocationControl<T> {

	private MethodInterceptorFilter<MockHandler<T>> invocationHandler;

	private Set<Method> mockedMethods;

	/**
	 * Initializes internal state.
	 * 
	 * @param invocationHandler
	 *            The mock invocation handler to be associated with this
	 *            instance.
	 * @param methodsToMock
	 *            The methods that are mocked for this instance. If
	 *            <code>methodsToMock</code> is null or empty, all methods for
	 *            the <code>invocationHandler</code> are considered to be
	 *            mocked.
	 */
	public MethodInvocationControlImpl(
			MethodInterceptorFilter<MockHandler<T>> invocationHandler,
			Set<Method> methodsToMock) {
		if (invocationHandler == null) {
			throw new IllegalArgumentException(
					"Invocation Handler cannot be null.");
		}

		if (methodsToMock == null) {
			methodsToMock = new HashSet<Method>();
		}

		this.invocationHandler = invocationHandler;
		this.mockedMethods = methodsToMock;
	}

	/**
	 * {@inheritDoc}
	 */
	public MethodInterceptorFilter<MockHandler<T>> getInvocationHandler() {
		return invocationHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Method> getMockedMethods() {
		return mockedMethods;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean isMocked(Method method) {
		return (mockedMethods.isEmpty() || mockedMethods.contains(method));
	}

	public boolean isInVerificationMode() {
		try {
			MockingProgress internalState = (MockingProgress) WhiteboxImpl
					.invokeMethod(ThreadSafeMockingProgress.class,
							"threadSafely");
			return WhiteboxImpl.getInternalState(internalState,
					VerificationMode.class) == null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
