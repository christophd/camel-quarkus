/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.camel.spi.ClassResolver;
import org.apache.camel.util.CastUtils;
import org.apache.camel.util.ObjectHelper;

public class CamelQuarkusClassResolver implements ClassResolver {

    private Set<ClassLoader> classLoaders;
    private final ClassLoader applicationContextClassLoader;

    public CamelQuarkusClassResolver(ClassLoader applicationContextClassLoader) {
        this.applicationContextClassLoader = applicationContextClassLoader;
    }

    @Override
    public void addClassLoader(ClassLoader classLoader) {
        if (classLoaders == null) {
            classLoaders = new LinkedHashSet<>();
        }
        classLoaders.add(classLoader);
    }

    @Override
    public Set<ClassLoader> getClassLoaders() {
        if (classLoaders == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(classLoaders);
    }

    @Override
    public Class<?> resolveClass(String name) {
        Class<?> result = loadClass(name, applicationContextClassLoader);
        if (result != null) {
            return result;
        }

        if (ObjectHelper.isNotEmpty(classLoaders)) {
            for (ClassLoader loader : classLoaders) {
                result = loadClass(name, loader);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public <T> Class<T> resolveClass(String name, Class<T> type) {
        return CastUtils.cast(loadClass(name, applicationContextClassLoader));
    }

    @Override
    public Class<?> resolveClass(String name, ClassLoader loader) {
        return loadClass(name, loader);
    }

    @Override
    public <T> Class<T> resolveClass(String name, Class<T> type, ClassLoader loader) {
        return CastUtils.cast(loadClass(name, loader));
    }

    @Override
    public Class<?> resolveMandatoryClass(String name) throws ClassNotFoundException {
        Class<?> answer = resolveClass(name);
        if (answer == null) {
            throw new ClassNotFoundException(name);
        }
        return answer;
    }

    @Override
    public <T> Class<T> resolveMandatoryClass(String name, Class<T> type) throws ClassNotFoundException {
        Class<T> answer = resolveClass(name, type);
        if (answer == null) {
            throw new ClassNotFoundException(name);
        }
        return answer;
    }

    @Override
    public Class<?> resolveMandatoryClass(String name, ClassLoader loader) throws ClassNotFoundException {
        Class<?> answer = resolveClass(name, loader);
        if (answer == null) {
            throw new ClassNotFoundException(name);
        }
        return answer;
    }

    @Override
    public <T> Class<T> resolveMandatoryClass(String name, Class<T> type, ClassLoader loader) throws ClassNotFoundException {
        Class<T> answer = resolveClass(name, type, loader);
        if (answer == null) {
            throw new ClassNotFoundException(name);
        }
        return answer;
    }

    @Override
    public InputStream loadResourceAsStream(String uri) {
        return ObjectHelper.loadResourceAsStream(uri, applicationContextClassLoader);
    }

    @Override
    public URL loadResourceAsURL(String uri) {
        return ObjectHelper.loadResourceAsURL(uri, applicationContextClassLoader);
    }

    @Override
    public Enumeration<URL> loadResourcesAsURL(String uri) {
        return loadAllResourcesAsURL(uri);
    }

    @Override
    public Enumeration<URL> loadAllResourcesAsURL(String uri) {
        return ObjectHelper.loadResourcesAsURL(uri);
    }

    protected Class<?> loadClass(String name, ClassLoader loader) {
        return ObjectHelper.loadClass(name, loader);
    }

}
