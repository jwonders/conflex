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

import org.junit.Test;

import com.jwsphere.conflex.Foo.CustomEnum;
import com.jwsphere.conflex.tools.ConflexAnalyzer;
import com.jwsphere.conflex.tools.ConflexHadoopConfigurationFileGenerator;
import com.jwsphere.conflex.tools.ConflexPropertiesFileGenerator;

public class FooTest {

    @Test
    public void injectProperties() {
        Properties p = new Properties();
        p.put("string_key", "string_value");
        p.put("long_key", "10");
        p.put("int_key", "100");
        p.put("float_key", "4.5");
        p.put("double_key", "9.5");
        p.put("Double_key", "5.8");
        p.put("custom_key", "custom_value");
        p.put("enum_key", "TYPE1");

        Foo foo = new Foo(p);
        assertEquals("string_value", foo.getStringValue());
        assertEquals(10l, foo.getLongValue());
        assertEquals(100, foo.getIntValue());
        assertEquals(4.5, foo.getFloatValue(), 1e-6);
        assertEquals(9.5, foo.getDoubleValue(), 1e-6);
        assertEquals(5.8, foo.getBigDoubleValue(), 1e-6);
        assertEquals("custom_value", foo.getCustomValue().value);
        assertEquals(CustomEnum.TYPE1, foo.getCustomEnumValue());
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
        assertEquals("custom_default", foo.getCustomValue().value);
        assertEquals(CustomEnum.DEFAULT, foo.getCustomEnumValue());
    }

    @Test
    public void injectMap() {
        Map<String, String> conf = new HashMap<String, String>();
        conf.put("string_key", "string_value");
        conf.put("long_key", "10");
        conf.put("int_key", "100");
        conf.put("float_key", "4.5");
        conf.put("double_key", "9.5");
        conf.put("custom_key", "custom_value");
        conf.put("enum_key", "TYPE1");

        Foo foo = new Foo(conf);
        assertEquals("string_value", foo.getStringValue());
        assertEquals(10l, foo.getLongValue());
        assertEquals(100, foo.getIntValue());
        assertEquals(4.5, foo.getFloatValue(), 1e-6);
        assertEquals(9.5, foo.getDoubleValue(), 1e-6);
        assertEquals("custom_value", foo.getCustomValue().value);
        assertEquals(CustomEnum.TYPE1, foo.getCustomEnumValue());
    }
    
    @Test
    public void injectObjectMap() {
        Map<Object, Object> conf = new HashMap<Object, Object>();
        conf.put("string_key", "string_value");
        conf.put("long_key", "10");
        conf.put("int_key", "100");
        conf.put("float_key", "4.5");
        conf.put("double_key", "9.5");
        conf.put("custom_key", "custom_value");
        conf.put("enum_key", "TYPE1");

        Foo foo = new Foo(conf);
        assertEquals("string_value", foo.getStringValue());
        assertEquals(10l, foo.getLongValue());
        assertEquals(100, foo.getIntValue());
        assertEquals(4.5, foo.getFloatValue(), 1e-6);
        assertEquals(9.5, foo.getDoubleValue(), 1e-6);
        assertEquals("custom_value", foo.getCustomValue().value);
        assertEquals(CustomEnum.TYPE1, foo.getCustomEnumValue());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void injectRawMap() {
        Map conf = new HashMap();
        conf.put("string_key", "string_value");
        conf.put("long_key", "10");
        conf.put("int_key", "100");
        conf.put("float_key", "4.5");
        conf.put("double_key", "9.5");
        conf.put("custom_key", "custom_value");
        conf.put("enum_key", "TYPE1");

        Foo foo = new Foo(conf);
        assertEquals("string_value", foo.getStringValue());
        assertEquals(10l, foo.getLongValue());
        assertEquals(100, foo.getIntValue());
        assertEquals(4.5, foo.getFloatValue(), 1e-6);
        assertEquals(9.5, foo.getDoubleValue(), 1e-6);
        assertEquals("custom_value", foo.getCustomValue().value);
        assertEquals(CustomEnum.TYPE1, foo.getCustomEnumValue());
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
        assertEquals("custom_default", foo.getCustomValue().value);
        assertEquals(CustomEnum.DEFAULT, foo.getCustomEnumValue());
    }

    @Test
    public void summary() {
        Conflex c = Conflex.create(Foo.class);
        System.out.println(c);
    }

    @Test
    public void template() {
        ConflexPropertiesFileGenerator generator = 
                new ConflexPropertiesFileGenerator(Foo.class);
        System.out.println(generator.generate());

        ConflexHadoopConfigurationFileGenerator hGenerator = 
                new ConflexHadoopConfigurationFileGenerator(Foo.class);
        System.out.println(hGenerator.generate());
    }

    @Test
    public void missing() {
        Map<String, String> conf = new HashMap<String, String>();
        conf.put("strin_key", "string_value"); // misspelling
        conf.put("long_key", "10");
        conf.put("int_key", "100");
        conf.put("float_key", "4.5");
        conf.put("double_key", "9.5");
        conf.put("custom_key", "custom_value");
        conf.put("enum_key", "TYPE1");

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
        conf.put("custom_key", "custom_value");
        conf.put("enum_key", "TYPE1");
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
