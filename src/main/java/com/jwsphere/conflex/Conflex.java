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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Conflex performs configuration injection using Java's reflection
 * facilities to alleviate the burden of managing large amounts of
 * simple configuration properties across a codebase.
 *
 * Conflex performs analysis of fields annotated with the {@link ConflexProperty}
 * annotation in order to build a mapping of the configuration object
 * to the class' fields.
 * 
 * @author jonathan.wonders
 */
public class Conflex {

	private static final Logger LOG = Logger.getLogger(Conflex.class);
	private static final Map<Class<?>, ConflexPropertyInjector> DEFAULT_TYPE_TO_INJECTOR_MAP;

	static {
		DEFAULT_TYPE_TO_INJECTOR_MAP = new HashMap<Class<?>, ConflexPropertyInjector>();
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(String.class, new StringPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(long.class, new LongParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(int.class, new IntParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(float.class, new FloatParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(double.class, new DoubleParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(Long.class, new BoxedLongParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(Integer.class, new BoxedIntParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(Float.class, new BoxedFloatParserPropertyInjector());
		DEFAULT_TYPE_TO_INJECTOR_MAP.put(Double.class, new BoxedDoubleParserPropertyInjector());
	}

	private List<ResolvedProperty> resolvedProperties;
	private Map<Class<?>, ConflexPropertyInjector> typeToInjectorMap;

	/**
	 * Constructs a conflex instance capable of injecting configuration
	 * values into the specified class.  Configuration fields must be
	 * annotated with the {@link ConflexProperty} annotation.
	 * 
	 * @param clazz
	 */
	public Conflex(Class<?> clazz) {
		this.resolvedProperties = new ArrayList<ResolvedProperty>();
		this.typeToInjectorMap = new HashMap<Class<?>, ConflexPropertyInjector>();
		typeToInjectorMap.putAll(DEFAULT_TYPE_TO_INJECTOR_MAP);

		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ConflexProperty.class)) {
				ConflexProperty property = field.getAnnotation(ConflexProperty.class);
				property.key();

				ConflexPropertyInjector injector = typeToInjectorMap.get(field.getType());

				if (injector != null) {
					ResolvedProperty rp = new ResolvedProperty();
					rp.p = property;
					rp.field = field;
					rp.injector = injector;

					resolvedProperties.add(rp);
				} else {
					LOG.warn("Unable to locate injector for " + field.getType().getCanonicalName());
				}
			}
		}
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
	public Conflex register(Class<?> clazz, ConflexPropertyInjector injector) {
		if (clazz != null && injector != null) {
			typeToInjectorMap.put(clazz, injector);
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
	public void inject(Object target, Properties properties) {
		for (ResolvedProperty rp : resolvedProperties) {
			String value = properties.getProperty(rp.p.key(), rp.p.defaultValue());
			rp.injector.inject(target, rp.field, value, rp.p.defaultValue());
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
	public void inject(Object target, Map conf) {
		for (ResolvedProperty rp : resolvedProperties) {
			Object value = conf.get(rp.p.key());
			if (value != null && value instanceof String) {
				rp.injector.inject(target, rp.field, (String) value, rp.p.defaultValue());
			} else {
				rp.injector.inject(target, rp.field, rp.p.defaultValue(), rp.p.defaultValue());
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
		ConflexPropertyInjector injector;
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
		}
		return properties;
	}

}
