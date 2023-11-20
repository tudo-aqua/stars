/*
 * Copyright 2023 The STARS Project Authors
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
 * Json format containing data for current tick.
 *
 * @property currentTick Current tick value.
 * @property actorPositions The current [JsonActorPosition]s of all actors.
 * @property weatherParameters The current [JSonDataWeatherParameters].
 */
@Serializable
data class JsonTickData(
    @SerialName("current_tick") val currentTick: Double,
    @SerialName("actor_positions") val actorPositions: List<JsonActorPosition>,
    @SerialName("weather_parameters") val weatherParameters: JSonDataWeatherParameters
)
