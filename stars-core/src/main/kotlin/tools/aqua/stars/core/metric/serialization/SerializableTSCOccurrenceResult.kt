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

package tools.aqua.stars.core.metric.serialization

import kotlinx.serialization.Serializable
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCOccurrence

/**
 * This class implements the [SerializableResult] interface and stores a [List] of
 * [SerializableTSCOccurrence]s.
 *
 * @property identifier The identifier of this specific result.
 * @property source The source (i.e. the metric) which produced this result.
 * @property count The size of the [value].
 * @property value The [List] of [SerializableTSCOccurrence]s.
 */
@Serializable
data class SerializableTSCOccurrenceResult(
    override val identifier: String,
    override val source: String,
    val count: Int,
    override val value: List<SerializableTSCOccurrence>,
) : SerializableResult()
