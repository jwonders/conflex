package com.jwsphere.conflex.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import com.jwsphere.conflex.Conflex;
import com.jwsphere.conflex.ConflexProperty;

public class ConflexHadoopConfigurationFileGenerator {

    private Collection<Class<?>> classes;
    boolean ignoreEmptyDefaults;

    /**
     * Constructs a generator for the specified classes.
     * 
     * @param classes The classes to generate a template for.
     */
    public ConflexHadoopConfigurationFileGenerator(Class<?> ... classes) {
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
    public ConflexHadoopConfigurationFileGenerator(Collection<Class<?>> classes) {
        this.classes = new ArrayList<Class<?>>(classes.size());
        this.classes.addAll(classes);
        this.ignoreEmptyDefaults = false;
    }

    public void generate(Writer writer) throws IOException {
        writer.append("<configuration>").append('\n');

        for (ConflexProperty property : Conflex.getAnnotatedProperties(classes)) {

            boolean includeProperty = !(ignoreEmptyDefaults && 
                    property.defaultValue().isEmpty());

            if (includeProperty) {
                writer.append("\t<property>").append('\n');
                writer.append("\t\t<name>").append(property.key())
                .append("</name>").append('\n');
                writer.append("\t\t<value>").append(property.defaultValue())
                .append("</value>").append('\n');
                // write a comment with the description if it is not empty
                if (!property.description().isEmpty()) {
                    writer.append("\t\t<description>").append(property.description())
                    .append("</description>").append('\n');
                }
                writer.append("\t</property>").append('\n');
            }
        }
        writer.append("</configuration>");
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
