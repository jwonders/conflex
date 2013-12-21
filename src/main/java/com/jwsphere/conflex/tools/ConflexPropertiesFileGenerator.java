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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

/**
 * This class provides the ability to generate the content for a properties
 * file from a set of classes annotated with {@link ConflexProperty}.
 * 
 * @author jonathan.wonders
 */
public class ConflexPropertiesFileGenerator {

    private Collection<Class<?>> classes;
    boolean ignoreEmptyDefaults;

    /**
     * Constructs a generator for the specified classes.
     * 
     * @param classes The classes to generate a template for.
     */
    public ConflexPropertiesFileGenerator(Class<?> ... classes) {
        this.classes = new ArrayList<Class<?>>(classes.length);
        for (int i = 0; i < classes.length; ++i) {
            this.classes.add(classes[i]);
        }
        this.ignoreEmptyDefaults = false;
    }

    /**
     * Constructs a generator for the specified classes.
     * 
     * @param classes The classes to generate a template for.
     */
    public ConflexPropertiesFileGenerator(Collection<Class<?>> classes) {
        this.classes = new ArrayList<Class<?>>(classes.size());
        this.classes.addAll(classes);
        this.ignoreEmptyDefaults = false;
    }

    /**
     * Configure the generator to ignore properties that have an
     * empty string as a default value.
     * 
     * @return
     */
    public ConflexPropertiesFileGenerator ignoreEmptyDefaults() {
        this.ignoreEmptyDefaults = true;
        return this;
    }

    /**
     * Generates a template of a properties file that is valid for
     * injection into the provided set of classes.  The lifecycle of
     * the writer is expected to be managed by the caller.  This method
     * will append to the writer so it is expected that the writer is
     * in the appropriate initial state.
     * 
     * @param writer A writer to which the properties file content is
     * appended.
     * 
     * @throws IOException 
     */
    public void generate(Writer writer) throws IOException {
        for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {
            boolean includeProperty = !(ignoreEmptyDefaults && 
                    property.defaultValue().isEmpty());

            if (includeProperty) {
                // write a comment with the description if it is not empty
                if (!property.description().isEmpty()) {
                    writer.append("# ").append(property.description()).append('\n');
                }
                writer.append(property.key()).append("=")
                .append(property.defaultValue()).append("\n\n");
            }
        }
    }

    public String generate() {
        StringWriter writer = new StringWriter();
        try {
            generate(writer);
        } catch (IOException e) {
            // suppressed
        }
        return writer.toString();
    }
}
