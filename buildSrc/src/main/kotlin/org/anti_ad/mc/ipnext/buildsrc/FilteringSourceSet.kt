/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.buildsrc

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetOutput

class FilteringSourceSet(val parent: SourceSet,
                         val filterString: String,
                         val logger: Logger): SourceSet {

    override fun getExtensions(): ExtensionContainer {
        return parent.extensions
    }

    override fun getName(): String {
        return parent.name
    }

    override fun getCompileClasspath(): FileCollection {
        val clean = parent.compileClasspath.filter {
            !it.name.contains(filterString)
        }
        return clean
    }

    override fun setCompileClasspath(classpath: FileCollection) {
        parent.compileClasspath = classpath
    }

    override fun getAnnotationProcessorPath(): FileCollection {
        return parent.annotationProcessorPath
    }

    override fun setAnnotationProcessorPath(annotationProcessorPath: FileCollection) {
        parent.annotationProcessorPath = annotationProcessorPath
    }

    override fun getRuntimeClasspath(): FileCollection {
        val clean = parent.runtimeClasspath.filter {
            !it.name.contains(filterString)
        }
        return clean
    }

    override fun setRuntimeClasspath(classpath: FileCollection) {
        parent.runtimeClasspath = classpath.filter {
            !it.name.contains(filterString)
        }
    }

    override fun getOutput(): SourceSetOutput {
        return parent.output
    }

    override fun compiledBy(vararg taskPaths: Any?): SourceSet {
        return parent.compiledBy(taskPaths)
    }

    override fun getResources(): SourceDirectorySet {
        return parent.resources
    }

    override fun resources(configureClosure: Closure<*>?): SourceSet {
        return parent.resources(configureClosure)
    }

    override fun resources(configureAction: Action<in SourceDirectorySet>): SourceSet {
        return parent.resources(configureAction)
    }

    override fun getJava(): SourceDirectorySet {
        return parent.java
    }

    override fun java(configureClosure: Closure<*>?): SourceSet {
        return parent.java(configureClosure)
    }

    override fun java(configureAction: Action<in SourceDirectorySet>): SourceSet {
        return parent.java(configureAction)
    }

    override fun getAllJava(): SourceDirectorySet {
        return parent.allJava
    }

    override fun getAllSource(): SourceDirectorySet {
        return parent.allSource
    }

    override fun getClassesTaskName(): String {
        return parent.classesTaskName
    }

    override fun getProcessResourcesTaskName(): String {
        return parent.processResourcesTaskName
    }

    override fun getCompileJavaTaskName(): String {
        return parent.compileJavaTaskName
    }

    override fun getCompileTaskName(language: String): String {
        return parent.getCompileTaskName(language)
    }

    override fun getJavadocTaskName(): String {
        return parent.javadocTaskName
    }

    override fun getJarTaskName(): String {
        return parent.jarTaskName
    }

    override fun getJavadocJarTaskName(): String {
        return parent.javadocJarTaskName
    }

    override fun getSourcesJarTaskName(): String {
        return parent.sourcesJarTaskName
    }

    override fun getTaskName(verb: String?,
                             target: String?): String {
        return parent.getTaskName(verb,
                                  target)
    }

    override fun getCompileOnlyConfigurationName(): String {
        return parent.compileOnlyConfigurationName
    }

    override fun getCompileOnlyApiConfigurationName(): String {
        return parent.compileOnlyApiConfigurationName
    }

    override fun getCompileClasspathConfigurationName(): String {
        return parent.compileClasspathConfigurationName
    }

    override fun getAnnotationProcessorConfigurationName(): String {
        return parent.annotationProcessorConfigurationName
    }

    override fun getApiConfigurationName(): String {
        return parent.apiConfigurationName
    }

    override fun getImplementationConfigurationName(): String {
        return parent.implementationConfigurationName
    }

    override fun getApiElementsConfigurationName(): String {
        return parent.apiElementsConfigurationName
    }

    override fun getRuntimeOnlyConfigurationName(): String {
        return parent.runtimeOnlyConfigurationName
    }

    override fun getRuntimeClasspathConfigurationName(): String {
        return parent.runtimeClasspathConfigurationName
    }

    override fun getRuntimeElementsConfigurationName(): String {
        return parent.runtimeElementsConfigurationName
    }

    override fun getJavadocElementsConfigurationName(): String {
        return parent.javadocElementsConfigurationName
    }

    override fun getSourcesElementsConfigurationName(): String {
        return parent.sourcesElementsConfigurationName
    }
}
