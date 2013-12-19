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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import com.jwsphere.conflex.Conflex;

public class FooTest {

	static {
		BasicConfigurator.configure();
	}
	
	@Test
	public void injectProperties() {
		Properties p = new Properties();
		p.put("string_key", "string_value");
		p.put("long_key", "10");
		p.put("int_key", "100");
		p.put("float_key", "4.5");
		p.put("double_key", "9.5");
		p.put("Double_key", "5.8");
		
		Foo foo = new Foo(p);
		assertEquals("string_value", foo.getStringValue());
		assertEquals(10l, foo.getLongValue());
		assertEquals(100, foo.getIntValue());
		assertEquals(4.5, foo.getFloatValue(), 1e-6);
		assertEquals(9.5, foo.getDoubleValue(), 1e-6);
		assertEquals(5.8, foo.getBigDoubleValue(), 1e-6);
	}
	
	@Test
	public void injectPropertiesDefault() {
		Properties p = new Properties();
		Foo foo = new Foo(p);
		assertEquals("default", foo.getStringValue());
		assertEquals(0l, foo.getLongValue());
		assertEquals(0, foo.getIntValue());
		assertEquals(0.0, foo.getFloatValue(), 1e-6);
		assertEquals(0.0, foo.getDoubleValue(), 1e-6);
	}
	
	@Test
	public void injectMap() {
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("string_key", "string_value");
		conf.put("long_key", "10");
		conf.put("int_key", "100");
		conf.put("float_key", "4.5");
		conf.put("double_key", "9.5");
		
		Foo foo = new Foo(conf);
		assertEquals("string_value", foo.getStringValue());
		assertEquals(10l, foo.getLongValue());
		assertEquals(100, foo.getIntValue());
		assertEquals(4.5, foo.getFloatValue(), 1e-6);
		assertEquals(9.5, foo.getDoubleValue(), 1e-6);
	}
	
	@Test
	public void injectMapDefault() {
		Map<String, String> conf = new HashMap<String, String>();
		Foo foo = new Foo(conf);

		assertEquals("default", foo.getStringValue());
		assertEquals(0l, foo.getLongValue());
		assertEquals(0, foo.getIntValue());
		assertEquals(0.0, foo.getFloatValue(), 1e-6);
		assertEquals(0.0, foo.getDoubleValue(), 1e-6);
	}
	
	@Test
	public void summary() {
		Conflex c = new Conflex(Foo.class);
		System.out.println(c);
	}
	
	@Test
	public void template() {
		ConflexGenerator generator = new ConflexGenerator(Foo.class);
		System.out.println(generator.generatePropertiesFileTemplate());
		System.out.println(generator.generateHadoopFileTemplate());
	}
	
	@Test
	public void missing() {
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("strin_key", "string_value"); // misspelling
		conf.put("long_key", "10");
		conf.put("int_key", "100");
		conf.put("float_key", "4.5");
		conf.put("double_key", "9.5");
		
		ConflexAnalyzer analyzer = new ConflexAnalyzer(Foo.class);
		Collection<String> missing = analyzer.findMissingProperties(conf);
		assertEquals(2, missing.size());
		
		System.out.println("Missing properties");
		for (String m : missing) {
			System.out.println(m);	
		}
	}

	@Test
	public void extra() {
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("string_key", "string_value");
		conf.put("long_key", "10");
		conf.put("int_key", "100");
		conf.put("float_key", "4.5");
		conf.put("double_key", "9.5");
		conf.put("extra_key", "extra_value");
		
		ConflexAnalyzer analyzer = new ConflexAnalyzer(Foo.class);
		Collection<String> extra = analyzer.findExtraProperties(conf);
		assertEquals(1, extra.size());
		
		System.out.println("Extra properties");
		for (String e : extra) {
			System.out.println(e);	
		}
	}

}
