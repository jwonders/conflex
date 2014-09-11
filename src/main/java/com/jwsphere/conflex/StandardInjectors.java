// Copyright 2013 Jonathan Wonders
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.jwsphere.conflex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class StandardInjectors {

	public static class StringInjector extends InjectorBase {
		@Override
		public void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.set(target, value);
		}
		@Override
		public void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(target, value);
		}
	}

	public static class PrimitiveBoolean extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.setBoolean(target, Boolean.parseBoolean(value));
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(this, Boolean.parseBoolean(value));
		}
	}

	public static class PrimitiveInteger extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.setInt(target, Integer.parseInt(value));
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(target, Integer.parseInt(value));
		}
	}

	public static class PrimitiveLong extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.setLong(target, Long.parseLong(value));
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(target, Long.parseLong(value));
		}
	}

	public static class PrimitiveFloat extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.setFloat(target, Float.parseFloat(value));
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(target, Float.parseFloat(value));
		}
	}

	public static class PrimitiveDouble extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
			field.setDouble(target, Double.parseDouble(value));
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
			method.invoke(target, Double.parseDouble(value));
		}
	}
	
	public static class EnumInjector extends InjectorBase {
	    @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
	    protected void parseAndInject(Object target, Field field, String value) throws IllegalAccessException {
	        field.set(target, Enum.valueOf((Class<Enum>) field.getType(), value));
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    @Override
	    protected void parseAndInject(Object target, Method method, String value) throws ReflectiveOperationException {
	        method.invoke(target, Enum.valueOf((Class<Enum>) method.getParameterTypes()[0], value));
	    }

	}

	public static class BoxedBoolean extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) {
			return Boolean.parseBoolean(value);
		}
	}

	public static class BoxedInteger extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) {
			return Integer.parseInt(value);
		}
	}

	public static class BoxedLong extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) {
			return Long.parseLong(value);
		}
	}

	public static class BoxedFloat extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) {
			return Float.parseFloat(value);
		}
	}

	public static class BoxedDouble extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) {
			return Double.parseDouble(value);
		}
	}

	/**
	 * The parser based injector implements a lot of boilerplate logic for
	 * setting either the value and default value.  To actually set the value
	 * this class delegates to the particular implementation.
	 */
	private abstract static class InjectorBase implements ConflexInjector {
		@Override
		public void inject(Object target, Field field, String value) throws InjectionException {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				parseAndInject(target, field, value);
			} catch (IllegalArgumentException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (ReflectiveOperationException e) {
				throw new InjectionException("Unable to inject the value", e);
			} catch (Exception e) {
				throw new InjectionException("Unanticipated exception during injection.", e);
			}
		}

		@Override
		public void inject(Object target, Method method, String value) throws InjectionException {
			try {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				parseAndInject(target, method, value);
			} catch (IllegalArgumentException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (ReflectiveOperationException e) {
				throw new InjectionException("Unable to inject the value", e);
			} catch (Exception e) {
				throw new InjectionException("Unanticipated exception during injection.", e);
			}
		}

		protected abstract void parseAndInject(Object target, Field f, String v) throws IllegalAccessException;
		protected abstract void parseAndInject(Object target, Method m, String v) throws ReflectiveOperationException;
	}

	public abstract static class ParserBasedObjectInjector implements ConflexInjector {
		@Override
		public void inject(Object target, Field field, String value) throws InjectionException {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object object = parse(value);
				field.set(target, object);
			} catch (IllegalArgumentException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (ReflectiveOperationException e) {
				throw new InjectionException("Unable to inject the value", e);
			} catch (Exception e) {
				throw new InjectionException("Unanticipated exception during injection.", e);
			}
		}

		@Override
		public void inject(Object target, Method method, String value) throws InjectionException {
			try {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				Object object = parse(value);
				method.invoke(target, object);
			} catch (IllegalArgumentException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (ReflectiveOperationException e) {
				throw new InjectionException("Unable to inject the value", e);
			} catch (Exception e) {
				throw new InjectionException("Unanticipated exception during injection.", e);
			}
		}

		protected abstract Object parse(String value);
	}

	private StandardInjectors() {
	}
}
