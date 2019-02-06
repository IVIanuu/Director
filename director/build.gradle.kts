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
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/android-build-lib.gradle")
apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/kt-android-ext.gradle")

android {
    defaultConfig {
        consumerProguardFile("proguard-rules.txt")
    }
    testOptions.unitTests.isIncludeAndroidResources = true
}

apply(from = "https://raw.githubusercontent.com/IVIanuu/gradle-scripts/master/mvn-publish.gradle")

dependencies {
    api(Deps.stdlibx)

    testImplementation(Deps.androidxTestCore)
    testImplementation(Deps.androidxTestJunit)
    testImplementation(Deps.androidxTestRules)
    testImplementation(Deps.androidxTestRunner)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockitoKotlin)
    testImplementation(Deps.roboelectric)
}