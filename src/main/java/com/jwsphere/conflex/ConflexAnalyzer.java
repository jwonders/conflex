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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class provides some utilities for analyzing the properties annotated
 * in a set of classes when compared to a configuration of some sort.
 * 
 * @author jonathan.wonders
 */
public class ConflexAnalyzer {

	private Class<?>[] classes;

	public ConflexAnalyzer(Class<?> ... classes) {
		this.classes = classes;
	}

	public Collection<String> findMissingProperties(Properties properties) {
		Collection<String> missing = new ArrayList<String>();
		for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {
			if (!properties.containsKey(property.key())) {
				missing.add(property.key());
			}
		}
		return missing;
	}

	@SuppressWarnings("rawtypes")
	public Collection<String> findMissingProperties(Map conf) {
		Collection<String> missing = new ArrayList<String>();
		for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {
			if (!conf.containsKey(property.key())) {
				missing.add(property.key());
			}
		}
		return missing;
	}

	public Collection<String> findExtraProperties(Properties properties) {
		SortedSet<String> propertyKeys = new TreeSet<String>();
		for (Object key : properties.keySet()) {
			if (key instanceof String) {
				String skey = (String) key;
				propertyKeys.add(skey);
			}
		}
		return findExtraProperties(propertyKeys);
	}

	@SuppressWarnings("rawtypes")
	public Collection<String> findExtraProperties(Map conf) {
		SortedSet<String> propertyKeys = new TreeSet<String>();
		for (Object key : conf.keySet()) {
			if (key instanceof String) {
				String skey = (String) key;
				propertyKeys.add(skey);
			}
		}
		return findExtraProperties(propertyKeys);
	}

	private SortedSet<String> findExtraProperties(SortedSet<String> confKeys) {
		Collection<ConflexProperty> cProperties = 
				Conflex.getAnnotatedProperties(classes);

		SortedSet<String> cPropertyKeys = new TreeSet<String>();
		for (ConflexProperty property : cProperties) {
			cPropertyKeys.add(property.key());
		}

		confKeys.removeAll(cPropertyKeys);
		return confKeys;
	}
}
