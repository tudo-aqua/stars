/*
 * Copyright 2021-2024 The STARS Project Authors
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
import org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_TASK_GROUP
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

  `java-library`
  `maven-publish`
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
      from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    }

val kdoc: Configuration by
    configurations.creating {
      isCanBeConsumed = true
      isCanBeResolved = false
    }

artifacts { add(kdoc.name, kdocJar) }

val javadocJar: TaskProvider<Jar> by
    tasks.registering(Jar::class) {
      archiveClassifier.set("javadoc")
      from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    }

java {
  withSourcesJar()
  withJavadocJar()
}

// black magic from https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
  dokkaGfmPlugin(libs.dokka.javadoc)
  testImplementation(libs.kotlin.test)
  detektPlugins(libs.detekt.rules.libraries)
}

tasks.test {
  useJUnitPlatform()
  testLogging { events(FAILED, PASSED, SKIPPED) }
}

kotlin { jvmToolchain(17) }

val mavenMetadata = extensions.create<MavenMetadataExtension>("mavenMetadata")

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name.set(mavenMetadata.name)
        description.set(mavenMetadata.description)

        val globalMetadata = rootProject.extensions.getByType<GlobalMavenMetadataExtension>()

        developers { globalMetadata.developers.get().forEach { developer(it.name, it.email) } }

        globalMetadata.githubProject.get().let {
          github(it.organization, it.project, it.mainBranch)
        }

        licenses { globalMetadata.licenses.get().forEach { license(it.name, it.url) } }
      }
    }
  }
}

signing {
  setRequired { gradle.taskGraph.allTasks.any { it.group == PUBLISH_TASK_GROUP } }
  useGpgCmd()
  sign(publishing.publications["maven"])
}
