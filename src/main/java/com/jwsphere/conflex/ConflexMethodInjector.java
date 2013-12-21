package com.jwsphere.conflex;

import java.lang.reflect.Method;

public interface ConflexMethodInjector {
	void inject(Object target, Method field, String value, String defaultValue) throws InjectionException;
}
