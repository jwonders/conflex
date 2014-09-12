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
package com.jwsphere.conflex.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

/**
 * This class provides some utilities for analyzing the properties annotated
 * in a set of classes when compared to a configuration of some sort.
 * 
 * @author jonathan.wonders
 */
public class ConflexAnalyzer {

    private Collection<Class<?>> classes;

    public ConflexAnalyzer(Class<?> ... classes) {
        this.classes = new ArrayList<Class<?>>(classes.length);
        for (int i = 0; i < classes.length; ++i) {
            this.classes.add(classes[i]);
        }
    }

    public ConflexAnalyzer(Collection<Class<?>> classes) {
        this.classes = new ArrayList<Class<?>>(classes.size());
        this.classes.addAll(classes);
    }

    public <U, V> Collection<String> findMissingProperties(Map<U, V> conf) {
        Collection<String> missing = new ArrayList<String>();
        for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {
            if (!conf.containsKey(property.key())) {
                missing.add(property.key());
            }
        }
        return missing;
    }

    public <U, V> Collection<String> findExtraProperties(Map<U, V> conf) {
        SortedSet<String> propertyKeys = new TreeSet<String>();
        for (Object key : conf.keySet()) {
            if (key instanceof String) {
                propertyKeys.add((String) key);
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
