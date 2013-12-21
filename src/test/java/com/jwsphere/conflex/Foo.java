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

import java.util.Map;
import java.util.Properties;

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

public final class Foo {

    private static final Conflex conflex = new Conflex(Foo.class);

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

    public Foo(Properties properties) {
        try {
            conflex.inject(this, properties);
        } catch (InjectionException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public Foo(Map conf) {
        try {
            conflex.inject(this, conf);
        } catch (InjectionException e) {
            throw new RuntimeException(e);
        }
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

}
