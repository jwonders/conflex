package com.jwsphere.conflex;

import java.lang.reflect.Field;

public class StandardInjectors {

	public static class StringInjector implements ConflexPropertyInjector {
		@Override
		public void inject(Object target, Field field, String value, String defaultValue) throws InjectionException {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				field.set(target, value);
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
			} catch (SecurityException e) {
				throw new InjectionException(e);
			}
		}
	};

	public static class PrimitiveBoolean extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				boolean b = Boolean.parseBoolean(value);
				field.setBoolean(target, b);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class PrimitiveInteger extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				int i = Integer.parseInt(value);
				field.setInt(target, i);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class PrimitiveLong extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				long l = Long.parseLong(value);
				field.setLong(target, l);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class PrimitiveFloat extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				float f = Float.parseFloat(value);
				field.setFloat(target, f);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class PrimitiveDouble extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				double d = Double.parseDouble(value);
				field.setDouble(target, d);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class BoxedBoolean extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				Boolean b = Boolean.parseBoolean(value);
				field.set(target, b);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class BoxedInteger extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				Integer i = Integer.parseInt(value);
				field.set(target, i);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class BoxedLong extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				Long l = Long.parseLong(value);
				field.set(target, l);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class BoxedFloat extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				Float f = Float.parseFloat(value);
				field.set(target, f);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

	public static class BoxedDouble extends ParserBasedInjector {
		@Override
		protected void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException {
			try {
				Double d = Double.parseDouble(value);
				field.set(target, d);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	};

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
	private static abstract class ParserBasedInjector implements ConflexPropertyInjector {
		@Override
		public void inject(Object target, Field field, String value, String defaultValue) throws InjectionException {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				try {
					inject(target, field, value);
				} catch (ParseException e) {
					inject(target, field, defaultValue);
				}
			} catch (IllegalAccessException e) {
				throw new InjectionException(e);
			} catch (SecurityException e) {
				throw new InjectionException(e);
			} catch (ParseException e) {
				throw new InjectionException("Unable to parse default value.", e);
			}
		}

		protected abstract void inject(Object target, Field field, String value)
				throws ParseException, IllegalArgumentException, IllegalAccessException;
	};
}
