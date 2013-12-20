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

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

/**
 * This class provides the ability to generate the content for a properties
 * file from a set of classes annotated with {@link ConflexProperty}.
 * 
 * @author jonathan.wonders
 */
public class ConflexGenerator {

	private Class<?>[] classes;
	boolean ignoreEmptyDefaults;

	/**
	 * Constructs a generator for the specified classes.
	 * 
	 * @param classes The classes to generate a template for.
	 */
	public ConflexGenerator(Class<?> ... classes) {
		this.classes = classes;
		this.ignoreEmptyDefaults = false;
	}
	
	/**
	 * Configure the generator to ignore properties that have an
	 * empty string as a default value.
	 * 
	 * @return
	 */
	public ConflexGenerator ignoreEmptyDefaults() {
		this.ignoreEmptyDefaults = true;
		return this;
	}

	/**
	 * Generates a template of a properties file that is valid for
	 * injection into the provided set of classes.
	 * 
	 * @return A valid properties file String.
	 */
	public String generatePropertiesFileTemplate() {
		StringBuilder content = new StringBuilder();

		for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {
			boolean includeProperty = !(ignoreEmptyDefaults && 
					property.defaultValue().isEmpty());

			if (includeProperty) {
				// write a comment with the description if it is not empty
				if (!property.description().isEmpty()) {
					content.append("# ").append(property.description()).append('\n');
				}
				content.append(property.key()).append("=")
				.append(property.defaultValue()).append("\n\n");
			}
		}
		return content.toString();
	}

	public String generateHadoopFileTemplate() {
		StringBuilder content = new StringBuilder();
		content.append("<configuration>").append('\n');
		
		for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {

			boolean includeProperty = !(ignoreEmptyDefaults && 
					property.defaultValue().isEmpty());

			if (includeProperty) {
				content.append("\t<property>").append('\n');
				content.append("\t\t<name>").append(property.key())
				.append("</name>").append('\n');
				content.append("\t\t<value>").append(property.defaultValue())
				.append("</value>").append('\n');
				// write a comment with the description if it is not empty
				if (!property.description().isEmpty()) {
					content.append("\t\t<description>").append(property.description())
					.append("</description>").append('\n');
				}
				content.append("\t</property>").append('\n');
			}
		}
		content.append("</configuration>");
		return content.toString();
	}
}
