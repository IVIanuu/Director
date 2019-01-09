import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.github.dcendents.android-maven")
}

group = "com.github.ivianuu"

android {
    compileSdkVersion(Build.compileSdk)

    defaultConfig {
        buildToolsVersion = Build.buildToolsVersion
        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdk)
        consumerProguardFile("proguard-rules.txt")
    }

    androidExtensions {
        // isExperimental = true
        configure(delegateClosureOf<AndroidExtensionsExtension> {
            isExperimental = true
        })
    }

    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(Deps.androidxFragment)
    api(Deps.kotlinStdLib)

    testImplementation(Deps.androidxTestCore)
    testImplementation(Deps.androidxTestJunit)
    testImplementation(Deps.androidxTestRules)
    testImplementation(Deps.androidxTestRunner)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockitoKotlin)
    testImplementation(Deps.roboelectric)
}

val sourcesJar = task("sourcesJar", Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    classifier = "sources"
}

val javadoc = task("javadoc", Javadoc::class) {
    isFailOnError = false
    source = android.sourceSets["main"].java.sourceFiles
    classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
    classpath += configurations.compile
}

val javadocJar = task("javadocJar", Jar::class) {
    dependsOn(javadoc)
    classifier = "javadoc"
    from(javadoc.destinationDir)
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}