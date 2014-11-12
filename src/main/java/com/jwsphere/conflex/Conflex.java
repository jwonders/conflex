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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jwsphere.conflex.StandardInjectors.BigDecimalInjector;
import com.jwsphere.conflex.StandardInjectors.BigIntegerInjector;
import com.jwsphere.conflex.StandardInjectors.BoxedBoolean;
import com.jwsphere.conflex.StandardInjectors.BoxedDouble;
import com.jwsphere.conflex.StandardInjectors.BoxedFloat;
import com.jwsphere.conflex.StandardInjectors.BoxedInteger;
import com.jwsphere.conflex.StandardInjectors.BoxedLong;
import com.jwsphere.conflex.StandardInjectors.EnumInjector;
import com.jwsphere.conflex.StandardInjectors.FileInjector;
import com.jwsphere.conflex.StandardInjectors.InetAddressInjector;
import com.jwsphere.conflex.StandardInjectors.PrimitiveBoolean;
import com.jwsphere.conflex.StandardInjectors.PrimitiveDouble;
import com.jwsphere.conflex.StandardInjectors.PrimitiveFloat;
import com.jwsphere.conflex.StandardInjectors.PrimitiveInteger;
import com.jwsphere.conflex.StandardInjectors.PrimitiveLong;
import com.jwsphere.conflex.StandardInjectors.StringInjector;
import com.jwsphere.conflex.StandardInjectors.URIInjector;
import com.jwsphere.conflex.StandardInjectors.URLInjector;

/**
 * Conflex performs configuration injection using Java's reflection facilities 
 * to alleviate the burden of managing large amounts of simple configuration 
 * properties across a codebase.
 *
 * Conflex performs analysis of fields annotated with the {@link ConflexProperty} 
 * annotation in order to build a mapping of the configuration object to the 
 * class' fields and methods.
 * 
 * @author jonathan.wonders
 */
public class Conflex {

    private static final ThreadLocal<Map<Class<?>, ConflexInjector>> DEFAULT_FIELD_INJECTOR_MAP;

    static {
        DEFAULT_FIELD_INJECTOR_MAP = new ThreadLocal<Map<Class<?>, ConflexInjector>>() {
            @Override
            protected Map<Class<?>, ConflexInjector> initialValue() {
                Map<Class<?>, ConflexInjector> map = new HashMap<Class<?>, ConflexInjector>();
                map.put(String.class, new StringInjector());
                map.put(boolean.class, new PrimitiveBoolean());
                map.put(int.class, new PrimitiveInteger());
                map.put(long.class, new PrimitiveLong());
                map.put(float.class, new PrimitiveFloat());
                map.put(double.class, new PrimitiveDouble());
                map.put(Boolean.class, new BoxedBoolean());
                map.put(Integer.class, new BoxedInteger());
                map.put(Long.class, new BoxedLong());
                map.put(Float.class, new BoxedFloat());
                map.put(Double.class, new BoxedDouble());
                map.put(BigInteger.class, new BigIntegerInjector());
                map.put(BigDecimal.class, new BigDecimalInjector());
                map.put(Enum.class, new EnumInjector());
                map.put(URL.class, new URLInjector());
                map.put(URI.class, new URIInjector());
                map.put(File.class, new FileInjector());
                map.put(InetAddress.class, new InetAddressInjector());
                return map;
            }
        };
    }

    private final List<ResolvedProperty> resolvedProperties;
    private final Map<Class<?>, ConflexInjector> injectors;
    private String prefix;

    private final Class<?> clazz;
    private volatile boolean dirty;

    public static Conflex create(final Class<?> clazz) {
        Conflex conflex = new Conflex(clazz);
        return conflex;
    }

    /**
     * Constructs a conflex instance capable of injecting configuration
     * values into the specified class.  Configuration fields must be
     * annotated with the {@link ConflexProperty} annotation.
     * 
     * @param clazz
     */
    private Conflex(Class<?> clazz) {
        this.resolvedProperties = new ArrayList<ResolvedProperty>();
        this.injectors = new HashMap<Class<?>, ConflexInjector>();
        this.clazz = clazz;
        this.prefix = "";
        this.dirty = true;
        injectors.putAll(DEFAULT_FIELD_INJECTOR_MAP.get());
    }

    private void resolve() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConflexProperty.class)) {
                ConflexInjector injector = findInjector(field.getType());
                if (injector != null) {
                    ConflexProperty property = field.getAnnotation(ConflexProperty.class);
                    resolvedProperties.add(new ResolvedProperty(property, field, null, injector));
                } 
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConflexProperty.class) && method.getParameterTypes().length == 1) {
                ConflexInjector injector = findInjector(method.getParameterTypes()[0]);
                if (injector != null) {
                    ConflexProperty property = method.getAnnotation(ConflexProperty.class);
                    resolvedProperties.add(new ResolvedProperty(property, null, method, injector));
                } 
            }
        }
        this.dirty = false;
    }

    /**
     * Attempts to find an injector that will produce a value that is assignable to
     * the field.
     * 
     * 1. Look for an injector registered for the concrete type.
     * 2. Check if the type is an enum.
     * 3. Check if there is an injector registered for a superclass.
     * 4. Check if there is an injector registered for an interface.
     * 
     * @param clazz The class to find an injector for.
     * @return An injector if found, null otherwise.
     */
    private ConflexInjector findInjector(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        ConflexInjector injector = injectors.get(clazz);
        if (injector == null && clazz.isEnum()) {
            injector = injectors.get(Enum.class);
        }
        return injector;
    }

    /**
     * Sets a custom key prefix that will be used to retrieve configuration values
     * from the input (e.g. if the ConflexProperty key is "key" and the prefix
     * for this conflex instance is set to "prefix.", the inject method will search
     * for the value associated with key "prefix.key".
     * 
     * To support a multi-threaded environment, the configuration class using conflex
     * is responsible for ensuring correct synchronization between the prefix and inject
     * method.  This can be achieved through external synchronization, by using thread-
     * local instances of conflex, or constructing a new instance each time it is used.
     * 
     * @param prefix - The prefix to use for associating key-values pairs with fields
     * annotated with {@link ConflexProperty}
     */
    public synchronized Conflex prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Registers a custom injector for the specified class.  This injector
     * is used to inject the configuration value for all fields that are
     * instances of this class.  There is currently no support for specifying
     * custom injectors on a field-by-field basis.
     * 
     * This method must be called before {{@link #inject(...)}
     * 
     * @param clazz The type for which this injector should be used.
     * @param injector The injector to use for the specified type.
     */
    public synchronized Conflex register(Class<?> clazz, ConflexInjector injector) {
        if (clazz != null && injector != null) {
            this.dirty = true;
            injectors.put(clazz, injector);
        }
        return this;
    }

    /**
     * For each property field, the corresponding value is extracted from
     * the provided map and given to the injector registered for the
     * field's type.  If the map value is of type java.lang.String, it is
     * ignored.
     * 
     * This method is typically called from the object's constructor.
     * 
     * @param target The object into which the configuration should be injected.
     * @param properties The properties to inject.
     */
    public synchronized <U, V> void inject(Object target, Map<U, V> conf) throws InjectionException {
        if (dirty) {
            resolve();
        }
        for (ResolvedProperty rp : resolvedProperties) {
            Object object = conf.get(prefix + rp.p.key());
            String value = rp.p.defaultValue();
            if (object instanceof String) {
                value = (String) object;
            } else if (object != null) {
                // support additional object types like Integer, Float, etc.
                // TODO support java list types?
                value = object.toString();
            }
            if (rp.field != null) {
                rp.injector.inject(target, rp.field, value);
            } else if (rp.method != null) {
                rp.injector.inject(target, rp.method, value);
            }
        }
    }
    
    /**
     * Constructs a string that describes the configuration instance after properties
     * have been injected.
     * 
     * @param instance
     * @return
     */
    public synchronized String describe(Object instance) {
        StringBuilder sb = new StringBuilder();
        
        for (ResolvedProperty rp : resolvedProperties) {
            String key = rp.p.key();
            String defaultValue = rp.p.defaultValue();
            String value = "[unknown - method properties not supported]";
            if (rp.field != null) {
                try {
                    value = rp.field.get(instance).toString();
                } catch (IllegalArgumentException e) {
                    value = "[unknown - error accessing field]";
                } catch (IllegalAccessException e) {
                    value = "[unknown - error accessing field]";
                }
            }
            sb.append(key).append(" = ");
            if (!(value.equals(defaultValue))) {
                sb.append(value).append(" ");
            }
            sb.append("[default value = ").append(defaultValue).append("]").append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (ResolvedProperty rp : resolvedProperties) {
            sb.append("{ key : ").append(rp.p.key()).append(" } ");
            sb.append("{ description : ").append(rp.p.description()).append(" } ");
            sb.append("{ type : ").append(rp.field.getType().getCanonicalName()).append(" } ");
            sb.append("{ default : ").append(rp.p.defaultValue()).append(" }\n");
        }
        return sb.toString();
    }

    /**
     * Holds data for a single property field of the class that this
     * object is capable of injecting configuration into.
     */
    private static class ResolvedProperty {
        ConflexProperty p;
        Field field;
        Method method;
        ConflexInjector injector;

        ResolvedProperty(ConflexProperty p, Field field, Method method, ConflexInjector injector) {
            this.p = p;
            this.field = field;
            this.method = method;
            this.injector = injector;
        }
    }

    /**
     * Returns a collection of the {@link ConflexProperty} annotations present
     * within the specified classes.
     * 
     * @param classes The classes to search for annotations.
     * @return A collection of the annotations found.
     */
    public static Collection<ConflexProperty> getAnnotatedProperties(final Class<?> ... classes) {
        return getAnnotatedProperties(toUnique(classes));
    }

    /**
     * Returns a collection of the {@link ConflexProperty} annotations present
     * within the specified classes.
     * 
     * @param classes The classes to search for annotations.
     * @return A collection of the annotations found.
     */
    public static Collection<ConflexProperty> getAnnotatedProperties(final Iterable<Class<?>> classes) {
        Collection<ConflexProperty> properties = new ArrayList<ConflexProperty>();
        for (Class<?> clazz : toUnique(classes)) {
            extractProperties(clazz, properties);
        }
        return properties;
    }

    /**
     * Returns a collection of the {@link ConflexProperty} annotations referenced
     * either directly within the supplied classes or through transitively 
     * evaluated classes identified through {@link ConflexModule#refs()}.
     * 
     * @param classes
     * @return
     */
    public static Set<ConflexProperty> getReferencedProperties(final Class<?> ... classes) {
        return getReferencedProperties(toUnique(classes));
    }

    /**
     * Returns a collection of the {@link ConflexProperty} annotations referenced
     * either directly within the supplied classes or through transitively 
     * evaluated classes identified through {@link ConflexModule#refs()}.
     * 
     * @param classes
     * @return
     */
    public static Set<ConflexProperty> getReferencedProperties(final Iterable<Class<?>> classes) {
        Set<ConflexProperty> properties = new HashSet<ConflexProperty>();
        Set<Class<?>> visited = new HashSet<Class<?>>();
        for (Class<?> clazz : toUnique(classes)) {
            extractReferencedProperties(clazz, visited, properties);
        }
        return properties;
    }

    private static void extractReferencedProperties(final Class<?> clazz,
            final Set<Class<?>> visited, final Set<ConflexProperty> properties) {
        extractProperties(clazz, properties);
        visited.add(clazz);
        if (clazz.isAnnotationPresent(ConflexModule.class)) {
            ConflexModule module = clazz.getAnnotation(ConflexModule.class);
            for (Class<?> ref : module.refs()) {
                if (!visited.contains(ref)) {
                    extractReferencedProperties(ref, visited, properties);
                }
            }
        }
    }

    private static void extractProperties(final Class<?> clazz, final Collection<ConflexProperty> properties) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConflexProperty.class)) {
                ConflexProperty property = field.getAnnotation(ConflexProperty.class);
                properties.add(property);
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConflexProperty.class)) {
                ConflexProperty property = method.getAnnotation(ConflexProperty.class);
                properties.add(property);
            }
        }
    }

    private static Set<Class<?>> toUnique(Class<?>[] classes) {
        Set<Class<?>> unique = new HashSet<Class<?>>();
        for (Class<?> clazz : classes) {
            unique.add(clazz);
        }
        return unique;
    }

    private static Set<Class<?>> toUnique(Iterable<Class<?>> classes) {
        if (classes instanceof Set) {
            return (Set<Class<?>>) classes;
        }
        Set<Class<?>> unique = new HashSet<Class<?>>();
        for (Class<?> clazz : classes) {
            unique.add(clazz);
        }
        return unique;
    }
}
