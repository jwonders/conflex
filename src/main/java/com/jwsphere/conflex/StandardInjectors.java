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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class StandardInjectors {

	public static class StringInjector extends InjectorBase {
		@Override
		public void parseAndInject(Object target, Field field, String value) 
				throws IllegalArgumentException, IllegalAccessException {
			field.set(target, value);
		}

		@Override
		public void parseAndInject(Object target, Method method, String value) 
				throws InjectionException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			method.invoke(target, value);
		}
	}

	public static class PrimitiveBoolean extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException {
			try {
				field.setBoolean(target, Boolean.parseBoolean(value));
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			try {
				method.invoke(this, Boolean.parseBoolean(value));
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class PrimitiveInteger extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException {
			try {
				field.setInt(target, Integer.parseInt(value));
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			try {
				method.invoke(target, Integer.parseInt(value));
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class PrimitiveLong extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException {
			try {
				long l = Long.parseLong(value);
				field.setLong(target, l);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			try {
				long l = Long.parseLong(value);
				method.invoke(target, l);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class PrimitiveFloat extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException {
			try {
				float f = Float.parseFloat(value);
				field.setFloat(target, f);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			try {
				float f = Float.parseFloat(value);
				method.invoke(target, f);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class PrimitiveDouble extends InjectorBase {
		@Override
		protected void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException {
			try {
				double d = Double.parseDouble(value);
				field.setDouble(target, d);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
		@Override
		protected void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				IllegalArgumentException, InvocationTargetException {
			try {
				double d = Double.parseDouble(value);
				method.invoke(target, d);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class BoxedBoolean extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) throws ParseException {
			try {
				return Boolean.parseBoolean(value);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class BoxedInteger extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) throws ParseException {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class BoxedLong extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) throws ParseException {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class BoxedFloat extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) throws ParseException {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	public static class BoxedDouble extends ParserBasedObjectInjector {
		@Override
		protected Object parse(String value) throws ParseException {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new ParseException(e);
			}
		}
	}

	/**
	 * An exception to generalize issues related to parsing a String
	 * as another value type.
	 */
	private static class ParseException extends Exception {
		private static final long serialVersionUID = 1L;

		public ParseException(Throwable cause) {
			super(cause);
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
			} catch (ParseException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
			} catch (SecurityException e) {
				throw new InjectionException(e);
			} catch (IllegalArgumentException e) {
				throw new InjectionException(e);
			}
		}

		@Override
		public void inject(Object target, Method method, String value) throws InjectionException {
			try {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				parseAndInject(target, method, value);
			} catch (ParseException e) {
				throw new InjectionException("Unable to parse value.", e);
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
			} catch (SecurityException e) {
				throw new InjectionException(e);
			} catch (IllegalArgumentException e) {
				throw new InjectionException(e);
			} catch (InvocationTargetException e) {
				throw new InjectionException(e);
			}
		}

		protected abstract void parseAndInject(Object target, Field field, String value)
				throws ParseException, IllegalAccessException;

		protected abstract void parseAndInject(Object target, Method method, String value)
				throws ParseException, IllegalAccessException, 
				InvocationTargetException, InjectionException, 
				IllegalArgumentException;
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
			} catch (ParseException e) {
				throw new InjectionException("Unable to parse default value.", e);
			} catch (IllegalArgumentException e) {
				throw new InjectionException(e);
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
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
			} catch (ParseException e) {
				throw new InjectionException("Unable to parse default value.", e);
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
			} catch (IllegalArgumentException e) {
				throw new InjectionException(e);
			} catch (InvocationTargetException e) {
				throw new InjectionException(e);
			}
		}

		protected abstract Object parse(String value) throws ParseException;
	}

	private StandardInjectors() {
	}
}
