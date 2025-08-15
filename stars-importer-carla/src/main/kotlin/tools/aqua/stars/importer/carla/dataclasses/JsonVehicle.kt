/*
 * Copyright 2023-2025 The STARS Project Authors
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

import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JSON object for vehicles.
 *
 * @property id The identifier of the vehicle.
 * @property location The [JsonLocation] of the vehicle.
 * @property rotation The [JsonRotation] of the vehicle.
 * @property typeId The type identifier, i.e. "vehicle.ford.mustang".
 * @property egoVehicle Whether this is the own vehicle.
 * @property forwardVector The current forward vector.
 * @property velocity The current velocity.
 * @property acceleration The current acceleration.
 * @property angularVelocity The current angular velocity.
 * @property attributes The additional attributes for the [JsonVehicle] from the CARLA simulation.
 * @property isAlive Whether the [JsonVehicle] is alive in the simulation.
 * @property isActive Whether the [JsonVehicle] is active in the simulation.
 * @property isDormant Whether the [JsonVehicle] is dormant in the simulation.
 * @property semanticTags The semantic tags of the [JsonVehicle] from the CARLA simulation.
 * @property boundingBox The bounding box of the [JsonVehicle].
 */
@Serializable
@SerialName("Vehicle")
@Suppress("BooleanPropertyNaming")
data class JsonVehicle(
    @SerialName("id") override val id: Int,
    @SerialName("location") override val location: JsonLocation,
    @SerialName("rotation") override val rotation: JsonRotation,
    @SerialName("type_id") val typeId: String,
    @SerialName("ego_vehicle") var egoVehicle: Boolean,
    @SerialName("forward_vector") val forwardVector: JsonVector3D,
    @SerialName("velocity") val velocity: JsonVector3D,
    @SerialName("acceleration") val acceleration: JsonVector3D,
    @SerialName("angular_velocity") val angularVelocity: JsonVector3D,
    @SerialName("attributes") override val attributes: Map<String, String>,
    @SerialName("is_alive") override val isAlive: Boolean,
    @SerialName("is_active") override val isActive: Boolean,
    @SerialName("is_dormant") override val isDormant: Boolean,
    @SerialName("semantic_tags") override val semanticTags: List<Int>,
    @SerialName("bounding_box") override val boundingBox: JsonBoundingBox?,
    @SerialName("collisions") override val collisions: List<Int>
) : JsonActor() {

  /** The effective velocity. */
  @Suppress("unused")
  val effVelocity: Double
    get() = sqrt(this.velocity.x.pow(2) + this.velocity.y.pow(2) + this.velocity.z.pow(2))
}
