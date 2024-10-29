/*
 * Copyright 2024 The STARS Project Authors
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

import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.serialization.extensions.getJsonString
import tools.aqua.stars.core.metric.utils.getJsonContentFromString

/**
 * Serializes all[SerializableResult]s of the given [serializable] and deserializes them again.
 *
 * @param serializable The [Serializable] from which all [SerializableResult]s should be
 *   (de)serialized.
 */
fun serializeAndDeserialize(serializable: Serializable): List<SerializableResult> =
    serializable
        .getSerializableResults()
        .map { it.getJsonString() }
        .map { getJsonContentFromString(it) }
