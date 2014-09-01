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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * A Conflex module represents a group of configuration values and can be
 * used by tools to enhance the generation of documentation and 
 * configuration file templates.
 * 
 * @author jonathan.wonders
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConflexModule {

    /**
     * The description describes a group of properties that are all
     * contained within a class definition.
     * @return
     */
    String description() default "";
    
    /**
     * Reference classes that are conflex-enabled to facilitate aggregating
     * all of the relevant configuration parameters into groups.
     * @return
     */
    Class<?>[] refs() default {};
}
