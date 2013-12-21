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

    public static class StringInjector implements ConflexFieldInjector, ConflexMethodInjector {
        @Override
        public void inject(Object target, Field field, String value) throws InjectionException {
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

        @Override
        public void inject(Object target, Method method, String value) throws InjectionException {
            try {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(target, value);
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
    }

    public static class PrimitiveBoolean extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                boolean b = Boolean.parseBoolean(value);
                field.setBoolean(target, b);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                boolean b = Boolean.parseBoolean(value);
                method.invoke(this, b);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class PrimitiveInteger extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                int i = Integer.parseInt(value);
                field.setInt(target, i);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                int i = Integer.parseInt(value);
                method.invoke(target, i);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class PrimitiveLong extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                long l = Long.parseLong(value);
                field.setLong(target, l);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                long l = Long.parseLong(value);
                method.invoke(target, l);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class PrimitiveFloat extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                float f = Float.parseFloat(value);
                field.setFloat(target, f);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                float f = Float.parseFloat(value);
                method.invoke(target, f);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class PrimitiveDouble extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                double d = Double.parseDouble(value);
                field.setDouble(target, d);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                double d = Double.parseDouble(value);
                method.invoke(target, d);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class BoxedBoolean extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                Boolean b = Boolean.parseBoolean(value);
                field.set(target, b);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                Boolean b = Boolean.parseBoolean(value);
                method.invoke(target, b);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class BoxedInteger extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                Integer i = Integer.parseInt(value);
                field.set(target, i);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                Integer i = Integer.parseInt(value);
                method.invoke(target, i);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class BoxedLong extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                Long l = Long.parseLong(value);
                field.set(target, l);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                Long l = Long.parseLong(value);
                method.invoke(target, l);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class BoxedFloat extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                Float f = Float.parseFloat(value);
                field.set(target, f);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                Float f = Float.parseFloat(value);
                method.invoke(target, f);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
    }

    public static class BoxedDouble extends ParserBasedInjector {
        @Override
        protected void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException {
            try {
                Double d = Double.parseDouble(value);
                field.set(target, d);
            } catch (Exception e) {
                throw new ParseException(e);
            }
        }
        @Override
        protected void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException {
            try {
                Double d = Double.parseDouble(value);
                method.invoke(target, d);
            } catch (Exception e) {
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
    private abstract static class ParserBasedInjector implements ConflexFieldInjector, ConflexMethodInjector {
        @Override
        public void inject(Object target, Field field, String value) throws InjectionException {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                parseAndInject(target, field, value);
            } catch (ParseException e) {
                throw new InjectionException("Unable to parse default value.", e);
            } catch (Exception e) {
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
                throw new InjectionException("Unable to parse default value.", e);
            } catch (Exception e) {
                throw new InjectionException(e);
            }
        }
        
        protected abstract void parseAndInject(Object target, Field field, String value)
                throws ParseException, IllegalAccessException;

        protected abstract void parseAndInject(Object target, Method method, String value)
                throws ParseException, IllegalAccessException, InvocationTargetException;
    }

    private StandardInjectors() {
    }
}
