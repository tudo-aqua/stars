/*
 * Copyright 2023-2026 The STARS Project Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.kotlin.dsl.`java-library`
import tools.aqua.*
import tools.aqua.GlobalMavenMetadataExtension
import tools.aqua.MavenMetadataExtension
import tools.aqua.developer
import tools.aqua.github
import tools.aqua.license

plugins {
  id("com.dorongold.task-tree")
  id("com.github.ben-manes.versions")
  id("com.diffplug.spotless")
  id("io.gitlab.arturbosch.detekt")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")

  `java-library`
  signing

  kotlin("jvm")
}

group = rootProject.group

version = rootProject.version

repositories { mavenCentral() }

tasks.dependencyUpdates {
  gradleReleaseChannel = "stable"
  rejectVersionIf(destabilizesVersion)
}

spotless {
  kotlinGradle { defaultFormat(rootProject) }
  kotlin { defaultFormat(rootProject) }
}

detekt {
  basePath = rootProject.projectDir.absolutePath
  config.setFrom(files(rootProject.file("contrib/detekt-rules.yml")))
}

val kdocJar: TaskProvider<Jar> by
    tasks.registering(Jar::class) {
      archiveClassifier.set("kdoc")
      from(tasks.dokkaGenerateHtml)
    }

val kdoc: Configuration by
    configurations.creating {
      isCanBeConsumed = true
      isCanBeResolved = false
    }

val tests by configurations.creating

val testJar by
    tasks.registering(Jar::class) {
      archiveClassifier.set("tests")
      from(sourceSets["test"].output)
    }

artifacts {
  add(kdoc.name, kdocJar)
  add(tests.name, testJar.get())
}

// black magic from https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
  testImplementation(libs.kotlin.test)
  detektPlugins(libs.detekt.rules.libraries)
}

tasks.test {
  useJUnitPlatform()
  testLogging { events(FAILED, PASSED, SKIPPED) }
}

kotlin { jvmToolchain(21) }

val mavenMetadata = extensions.create<MavenMetadataExtension>("mavenMetadata")

mavenPublishing {
  publishToMavenCentral()
  signAllPublications()

  pom {
    name.set(mavenMetadata.name)
    description.set(mavenMetadata.description)

    val globalMetadata = rootProject.extensions.getByType<GlobalMavenMetadataExtension>()

    developers { globalMetadata.developers.get().forEach { developer(it.name, it.email) } }

    globalMetadata.githubProject.get().let { github(it.organization, it.project, it.mainBranch) }

    licenses { globalMetadata.licenses.get().forEach { license(it.name, it.url) } }
  }
}

signing { useGpgCmd() }
