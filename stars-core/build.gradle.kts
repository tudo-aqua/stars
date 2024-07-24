/*
 * Copyright 2022-2024 The STARS Project Authors
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

plugins { id("tools.aqua.stars.library-conventions") }

mavenMetadata {
  name.set("STARS Core Library")
  description.set("STARS - Scenario-Based Testing of Autonomous Robotic Systems - Core Library")
}

dependencies {
  implementation(libs.letsplot.kotlinjvm)
  implementation(libs.letsplot.imageexport)
  implementation(libs.slf4j.api)
  implementation(libs.slf4j.simple)
}
