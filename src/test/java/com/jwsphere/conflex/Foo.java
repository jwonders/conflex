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
import java.util.Map;
import java.util.Properties;

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

public final class Foo {

    /**
     * Static instances of conflex are synchronized for use in multi-threaded
     * environments but if different prefixes need to be set in different threads
     * for the same configuration class, external synchronization is required.  In
     * this case it is advised to use thread-local instances.
     */
    // private static final Conflex conflex = 
    // Conflex.create(Foo.class).register(CustomType.class, new CustomInjector());
    
    /**
     * Using thread-local conflex instances achieves much higher performance of
     * injection compared to instantiating a conflex instance for each injection
     * and is safe to use in multi-threaded environments.
     * 
     * Performance is only noticible when constructing MANY instances of configuration
     * classes.  This is rarely the case and if it is, you should re-consider the design.
     */
    private static final ThreadLocal<Conflex> conflex = new ThreadLocal<Conflex>() {
        @Override
        protected Conflex initialValue() {
            return Conflex.create(Foo.class).register(CustomType.class, new CustomInjector());
        }
    };

    public static final String STRING_KEY = "string_key";

    @ConflexProperty(key = STRING_KEY, defaultValue = "default", description = "a string")
    private String stringValue;

    @ConflexProperty(key = "long_key", defaultValue = "0", description = "a long")
    private long longValue;

    @ConflexProperty(key = "int_key", defaultValue = "0", description = "an int")
    private int intValue;

    @ConflexProperty(key = "float_key", defaultValue = "0.0", description = "a float")
    private float floatValue;

    @ConflexProperty(key = "double_key", defaultValue = "0.0", description = "a double")
    private double doubleValue;

    @ConflexProperty(key = "Double_key", defaultValue = "1.0", description = "a Double")
    private Double bigDoubleValue;

    @ConflexProperty(key = "custom_key", defaultValue = "custom_default", description = "a custom object")
    private CustomType customValue;

    @ConflexProperty(key = "enum_key", defaultValue = "DEFAULT", description = "a custom enum setting")
    private CustomEnum customEnum;

    public Foo(Properties properties) {
        conflex.get().inject(this, properties);
    }

    public Foo(Map<?, ?> conf) {
        conflex.get().inject(this, conf);
    }

    public String getStringValue() {
        return stringValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public Double getBigDoubleValue() {
        return bigDoubleValue;
    }

    public CustomType getCustomValue() {
        return customValue;
    }

    public CustomEnum getCustomEnumValue() {
        return customEnum;
    }

    public static final class CustomType {
        public String value;
    }

    public static enum CustomEnum {
        TYPE1,
        DEFAULT
    }

    public static final class CustomInjector implements ConflexInjector {
        @Override
        public void inject(Object target, Field field, String value) throws InjectionException {
            CustomType object = new CustomType();
            object.value = value;
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(target, object);
            } catch (IllegalArgumentException e) {
                throw new InjectionException(e);
            } catch (IllegalAccessException e) {
                throw new InjectionException(e);
            }
        }

        @Override
        public void inject(Object target, Method method, String value) throws InjectionException {
            CustomType object = new CustomType();
            object.value = value;
            try {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(target, object);
            } catch (IllegalArgumentException e) {
                throw new InjectionException(e);
            } catch (IllegalAccessException e) {
                throw new InjectionException(e);
            } catch (InvocationTargetException e) {
                throw new InjectionException(e);
            }
        }
    }
}
