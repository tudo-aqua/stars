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

package tools.aqua.stars.core.serialization

import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.serialization.extensions.getJsonString
import tools.aqua.stars.core.utils.getJsonContentFromString

/**
 * Serializes all[SerializableResult]s of the given [serializableMetric] and deserializes them
 * again.
 *
 * @param serializableMetric The [SerializableMetric] from which all [SerializableResult]s should be
 *   (de)serialized.
 */
fun serializeAndDeserialize(serializableMetric: SerializableMetric): List<SerializableResult> =
    serializableMetric
        .getSerializableResults()
        .map { it.getJsonString() }
        .map { getJsonContentFromString(it) }
