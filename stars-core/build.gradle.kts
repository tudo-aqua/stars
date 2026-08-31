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

import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
  `java-test-fixtures`
  kotlin("plugin.serialization")
}

extensions.configure<MavenPublishBaseExtension> {
  pom {
    name.set("STARS Core Library")
    description.set("STARS - Scenario-Based Testing of Autonomous Robotic Systems - Core Library")
  }
}

dependencies {
  implementation(libs.letsplot.kotlinjvm)
  implementation(libs.letsplot.imageexport)
  implementation(libs.slf4j.api)
  implementation(libs.slf4j.simple)
  implementation(libs.kotlinx.serialization.json)
  testFixturesApi(libs.kotlin.test)
  testFixturesApi(libs.junit.jupiter)
}

// Ensure the testFixtures component is published via a dedicated publication
publishing {
  publications {
    create<MavenPublication>("testFixtures") {
      val testFixturesComponent = components.findByName("testFixtures")
      if (testFixturesComponent != null) {
        from(testFixturesComponent)
      }
    }
  }
}
