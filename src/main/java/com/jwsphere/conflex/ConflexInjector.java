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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A conflex property injector performs the work of injecting a single
 * value or the associated default value into the target's field.
 * 
 * @author jonathan.wonders
 */
public interface ConflexInjector {

    /**
     * Injects a single value into a field in the target object.
     * 
     * @param target The object into which the value is to be injected.
     * @param field The field to inject the value into.
     * @param value The primary value to inject.
     * 
     * @throws InjectionException
     */
    void inject(Object target, Field field, String value) throws InjectionException;

    /**
     * Injects a single value by invoking a setter method in the target object.
     * 
     * @param target The object into which the value is to be injected.
     * @param field The field to inject the value into.
     * @param value The value to inject.
     * 
     * @throws InjectionException
     */
    void inject(Object target, Method field, String value) throws InjectionException;
}
