/*
 * Copyright 2023-2024 The STARS Project Authors
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

package tools.aqua.stars.data.av.dataclasses

/**
 * Data class for road touching points.
 *
 * @property id Identifier of the road touching point.
 * @property contactLocation The [Location] of the touching point.
 * @property lane1 [Lane] 1.
 * @property lane1StartPos Start position on lane 1.
 * @property lane1EndPos End position on lane 1.
 * @property lane2 [Lane] 2.
 * @property lane2StartPos Start position on lane 2.
 * @property lane2EndPos End position on lane 2.
 */
data class ContactArea(
    val id: String,
    val contactLocation: Location,
    val lane1: Lane,
    val lane1StartPos: Double,
    val lane1EndPos: Double,
    val lane2: Lane,
    val lane2StartPos: Double,
    val lane2EndPos: Double
)
