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

	@ConflexProperty(key = "string_key", defaultValue = "default", description = "a string")
	private String stringValue;

	@ConflexProperty(key = "long_key", defaultValue = "0", description = "a long")
	private long longValue;

	@ConflexProperty(key = "int_key", defaultValue = "0", description = "an int")
	private int intValue;

	@ConflexProperty(key = "float_key", defaultValue = "0.0", description = "a float")
	private float floatValue;

	@ConflexProperty(key = "double_key", defaultValue = "0.0", description = "a double")
	private double doubleValue;

	public Foo(Properties properties) {
		conflex.inject(this, properties);
	}

	@SuppressWarnings("rawtypes")
	public Foo(Map conf) {
		conflex.inject(this, conf);
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

}
