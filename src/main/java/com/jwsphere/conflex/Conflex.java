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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jwsphere.conflex.StandardInjectors.BoxedBoolean;
import com.jwsphere.conflex.StandardInjectors.BoxedInteger;
import com.jwsphere.conflex.StandardInjectors.BoxedLong;
import com.jwsphere.conflex.StandardInjectors.BoxedFloat;
import com.jwsphere.conflex.StandardInjectors.BoxedDouble;
import com.jwsphere.conflex.StandardInjectors.PrimitiveBoolean;
import com.jwsphere.conflex.StandardInjectors.PrimitiveDouble;
import com.jwsphere.conflex.StandardInjectors.PrimitiveFloat;
import com.jwsphere.conflex.StandardInjectors.PrimitiveInteger;
import com.jwsphere.conflex.StandardInjectors.PrimitiveLong;
import com.jwsphere.conflex.StandardInjectors.StringInjector;

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
				return map;
			}
		};
	}

	private List<ResolvedProperty> resolvedProperties;
	private Map<Class<?>, ConflexInjector> injectors;

	private Class<?> clazz;
	private boolean dirty;

	public static Conflex create(final Class<?> clazz) {
		return new Conflex(clazz);
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
		this.dirty = true;
		injectors.putAll(DEFAULT_FIELD_INJECTOR_MAP.get());
		resolve();
	}

	private void resolve() {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ConflexProperty.class)) {
				ConflexProperty property = field.getAnnotation(ConflexProperty.class);
				property.key();

				ConflexInjector injector = injectors.get(field.getType());

				if (injector != null) {
					ResolvedProperty rp = new ResolvedProperty();
					rp.p = property;
					rp.field = field;
					rp.injector = injector;

					resolvedProperties.add(rp);
				} 
			}
		}

		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(ConflexProperty.class) && method.getParameterTypes().length == 1) {
				ConflexProperty property = method.getAnnotation(ConflexProperty.class);
				property.key();

				Class<?> parameterType = method.getParameterTypes()[0];

				ConflexInjector injector = injectors.get(parameterType);

				if (injector != null) {
					ResolvedProperty rp = new ResolvedProperty();
					rp.p = property;
					rp.method = method;
					rp.injector = injector;

					resolvedProperties.add(rp);
				} 
			}
		}

		this.dirty = false;
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
	public Conflex register(Class<?> clazz, ConflexInjector injector) {
		if (clazz != null && injector != null) {
			this.dirty = true;
			injectors.put(clazz, injector);
		}
		return this;
	}

	/**
	 * For each property field, the corresponding value is extracted from
	 * the provided properties and given to the injector registered for the
	 * field's type.
	 * 
	 * This method is typically called from the object's constructor.
	 * 
	 * @param target The object into which the configuration should be injected.
	 * @param properties The properties to inject.
	 */
	public void inject(Object target, Properties properties) throws InjectionException {
		if (dirty) {
			resolve();
		}
		for (ResolvedProperty rp : resolvedProperties) {
			String value = properties.getProperty(rp.p.key(), rp.p.defaultValue());
			if (value == null) {
				value = rp.p.defaultValue();
			}
			if (rp.field != null) {
				rp.injector.inject(target, rp.field, value);
			} else if (rp.method != null) {
				rp.injector.inject(target, rp.method, value);
			}
		}
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
	@SuppressWarnings("rawtypes")
	public void inject(Object target, Map conf) throws InjectionException {
		if (dirty) {
			resolve();
		}
		for (ResolvedProperty rp : resolvedProperties) {
			Object object = conf.get(rp.p.key());
			String value = rp.p.defaultValue();
			if (object instanceof String) {
				value = (String) object;
			}
			if (rp.field != null) {
				rp.injector.inject(target, rp.field, value);
			} else if (rp.method != null) {
				rp.injector.inject(target, rp.method, value);
			}
		}
	}

	@Override
	public String toString() {
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
	}

	/**
	 * Returns a collection of the {@link ConflexProperty} annotations present
	 * within the specified classes.
	 * 
	 * @param classes The classes to search for annotations.
	 * @return A collection of the annotations found.
	 */
	public static Collection<ConflexProperty> getAnnotatedProperties(Class<?> ... classes) {
		Collection<ConflexProperty> properties = new ArrayList<ConflexProperty>();
		for (Class<?> clazz : classes) {
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

		return properties;
	}

	/**
	 * Returns a collection of the {@link ConflexProperty} annotations present
	 * within the specified classes.
	 * 
	 * @param classes The classes to search for annotations.
	 * @return A collection of the annotations found.
	 */
	public static Collection<ConflexProperty> getAnnotatedProperties(Iterable<Class<?>> classes) {
		Collection<ConflexProperty> properties = new ArrayList<ConflexProperty>();
		for (Class<?> clazz : classes) {
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
		return properties;
	}

}
