/*
 * Copyright 2026 The STARS Project Authors
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
 * Describes a single lane marking that an [Actor]'s world-space [BoundingBox] is currently touching
 * (or has crossed) in a given tick.
 *
 * An empty [Actor.laneMarkingContacts] list therefore means the bounding box lies within its lane's
 * markings.
 *
 * @property side The [ContactSide] of the reference [Lane], in driving direction.
 * @property roadId The identifier of the reference [Road].
 * @property laneId The identifier of the reference [Lane].
 * @property marking The touched [LaneMarking], or `null` if unavailable.
 * @property isCrossing Whether the bounding box reaches past the outer edge of the marking band,
 *   i.e. it overlaps the neighbouring lane rather than merely touching the marking.
 * @property penetration How far (in meters) the box extends past the inner edge of the marking
 *   band.
 */
data class LaneMarkingContact(
    val side: ContactSide,
    val roadId: Int,
    val laneId: Int,
    val marking: LaneMarking?,
    val isCrossing: Boolean,
    val penetration: Double,
)
