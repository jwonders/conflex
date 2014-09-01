package com.jwsphere.conflex;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

public class ModuleTest {

	@Test
	public void test() {
		Set<ConflexProperty> properties = Conflex.getReferencedProperties(Module.class);
		assertEquals(8, properties.size());
		for (ConflexProperty property : properties) {
			System.out.println(property);
		}
	}
	
}
