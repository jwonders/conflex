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

import java.util.HashMap;
import java.util.Map;

/**
 * An example of creating a configuration 
 * 
 * @author jonathan.wonders
 */
public final class Bar {

    private static Conflex conflex = Conflex.create(Bar.class);

    public static final String FOO_KEY = "foo";
    
    private final Map<String, String> dynamicStorage;

    public Bar(Map<?, ?> conf) { 
        this.dynamicStorage = new HashMap<String, String>();
        try {
            conflex.inject(this, conf);
        } catch (InjectionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFoo() {
        return dynamicStorage.get(FOO_KEY);
    }

    @ConflexProperty(key = FOO_KEY, defaultValue = "default")
    public void set(String foo) {
        dynamicStorage.put(FOO_KEY, foo);
    }
    
}
