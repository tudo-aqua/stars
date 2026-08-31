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

import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentFilter
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
  alias(libs.plugins.task.tree)
  alias(libs.plugins.versions)
  alias(libs.plugins.spotless)
  alias(libs.plugins.git.versioning)

  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.maven.publish) apply false
}

group = "tools.aqua"

repositories { mavenCentral() }

val stableKeyword = listOf("RELEASE", "FINAL", "GA")

val stableRegex = "^[0-9,.v-]+(-r)?$".toRegex()

fun String.isStable() =
    stableKeyword.any { contains(it, ignoreCase = true) } || stableRegex.matches(this)

fun destabilizesVersion() = ComponentFilter {
  candidate.version.isStable().not() && currentVersion.isStable()
}

tasks.dependencyUpdates {
  gradleReleaseChannel = "stable"
  rejectVersionIf(destabilizesVersion())
}

spotless {
  kotlinGradle {
    licenseHeaderFile(
            rootProject.file("contrib/license-header.template.kt"),
            "(import |@file|plugins |dependencyResolutionManagement|rootProject.name)",
        )
        .also { it.updateYearWithLatest(true) }
    ktfmt()
  }
}

gitVersioning.apply {
  describeTagPattern = "v(?<version>.*)"
  refs {
    considerTagsOnBranches = true
    tag("v(?<version>.*)") { version = "\${ref.version}" }
    branch("((?!main).*|main.+|)") { // everything but main
      version =
          "\${describe.tag.version}-\${ref.slug}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
    branch("main") {
      version = "\${describe.tag.version}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
  }
}

val catalogLibs = libs

subprojects {
  apply(plugin = "com.dorongold.task-tree")
  apply(plugin = "com.github.ben-manes.versions")
  apply(plugin = "com.diffplug.spotless")
  apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "org.jetbrains.dokka")
  apply(plugin = "com.vanniktech.maven.publish")
  apply(plugin = "java-library")
  apply(plugin = "signing")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  group = rootProject.group

  version = rootProject.version

  repositories { mavenCentral() }

  tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    gradleReleaseChannel = "stable"
    rejectVersionIf(destabilizesVersion())
  }

  extensions.configure<SpotlessExtension> {
    kotlinGradle {
      licenseHeaderFile(
              rootProject.file("contrib/license-header.template.kt"),
              "(import |@file|plugins |dependencyResolutionManagement|rootProject.name)",
          )
          .also { it.updateYearWithLatest(true) }
      ktfmt()
    }
    kotlin {
      licenseHeaderFile(rootProject.file("contrib/license-header.template.kt")).also {
        it.updateYearWithLatest(true)
      }
      ktfmt()
    }
  }

  extensions.configure<DetektExtension> {
    basePath = rootProject.projectDir.absolutePath
    config.setFrom(files(rootProject.file("contrib/detekt-rules.yml")))
  }

  val kdocJar =
      tasks.register<Jar>("kdocJar") {
        description = "Assembles a jar archive containing the generated KDoc API documentation."
        archiveClassifier.set("kdoc")
        from(tasks.named("dokkaGenerateHtml"))
      }

  val kdoc =
      configurations.create("kdoc") {
        isCanBeConsumed = true
        isCanBeResolved = false
      }

  val tests = configurations.create("tests")

  val testJar =
      tasks.register<Jar>("testJar") {
        description = "Assembles a jar archive containing the compiled test classes."
        archiveClassifier.set("tests")
        from(project.the<SourceSetContainer>()["test"].output)
      }

  artifacts {
    add(kdoc.name, kdocJar.get())
    add(tests.name, testJar.get())
  }

  dependencies {
    add("testImplementation", catalogLibs.kotlin.test)
    add("detektPlugins", catalogLibs.detekt.rules.libraries)
  }

  tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging { events(FAILED, PASSED, SKIPPED) }
  }

  extensions.configure<KotlinJvmProjectExtension> { jvmToolchain(21) }

  extensions.configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()

    pom {
      url.set("https://github.com/tudo-aqua/stars")

      scm {
        connection.set("scm:git:git://github.com:tudo-aqua/stars.git")
        developerConnection.set("scm:git:ssh://git@github.com:tudo-aqua/stars.git")
        url.set("https://github.com/tudo-aqua/stars/tree/main")
      }

      developers {
        developer {
          name.set("Till Schallau")
          email.set("till.schallau@tu-dortmund.de")
        }
        developer {
          name.set("Stefan Naujokat")
          email.set("stefan.naujokat@tu-dortmund.de")
        }
        developer {
          name.set("Fiona Kullmann")
          email.set("fiona.kullmann@tu-dortmund.de")
        }
        developer {
          name.set("Falk Howar")
          email.set("falk.howar@tu-dortmund.de")
        }
        developer {
          name.set("Nick Pawlinorz")
          email.set("nick.pawlinorz@tu-dortmund.de")
        }
        developer {
          name.set("Dominik Schmid")
          email.set("dominik.schmid@tu-dortmund.de")
        }
      }

      licenses {
        license {
          name.set("Apache License, Version 2.0")
          url.set("https://opensource.org/licenses/Apache-2.0")
        }
      }
    }
  }

  extensions.configure<SigningExtension> { useGpgCmd() }
}
