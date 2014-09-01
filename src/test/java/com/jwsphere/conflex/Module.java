package com.jwsphere.conflex;

import java.util.Map;

/**
 * Identify to Conflex that in order to configure this module, the
 * properties that are required by Foo and Bar must be provided.
 */
@ConflexModule(refs = {Foo.class, Bar.class})
public class Module {
	
	private Foo foo; 
	private Bar bar;
	
	public Module(Map<?, ?> conf) { 
		this.foo = new Foo(conf);
		this.bar = new Bar(conf);
	}
	
	public Foo getFoo() {
		return foo;
	}
	
	public Bar getBar() {
		return bar;
	}
	
}
