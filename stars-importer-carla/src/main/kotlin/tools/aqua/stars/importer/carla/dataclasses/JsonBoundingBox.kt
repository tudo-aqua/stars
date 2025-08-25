/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Json object for [JsonActor] bounding boxes.
 *
 * @property extent The extent of the bounding box.
 * @property location The location of the bounding box.
 * @property rotation The rotation of the bounding box.
 * @property vertices All vertices of the bounding box.
 */
@Serializable
data class JsonBoundingBox(
    val extent: JsonVector3D,
    val location: JsonLocation,
    val rotation: JsonRotation,
    val vertices: List<JsonLocation>
)
