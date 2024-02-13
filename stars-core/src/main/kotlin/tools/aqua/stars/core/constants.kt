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

package tools.aqua.stars.core

/** Valid instances projection name. */
const val VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME: String =
    "valid-tsc-instances-per-projection"

/** Valid instances occurrences name. */
const val VALID_TSC_INSTANCES_OCCURRENCES_PER_PROJECTION_METRIC_NAME: String =
    "valid-tsc-instances-occurrences-per-projection"

/** The default minimal tick count per segment to load. */
const val DEFAULT_MIN_SEGMENT_TICK_COUNT: Int = 10

/** The default size of the simulation run prefetch buffer. */
const val DEFAULT_SIMULATION_RUN_PREFETCH_SIZE: Int = 1

/** The default number of slice threads. */
const val DEFAULT_NUM_SLICE_THREADS: Int = 1

/** The default size of the simulation run prefetch buffer. */
const val DEFAULT_SEGMENT_PREFETCH_SIZE: Int = 500

// region Terminal colors
/** Terminal color red. */
const val RED = "\u001b[31m"

/** Terminal color orange. */
const val ORANGE = "\u001b[33m"

/** Terminal color yellow. */
const val YELLOW = "\u001b[93m"

/** Terminal color light green. */
const val LIGHT_GREEN = "\u001b[92m"

/** Terminal color green. */
const val GREEN = "\u001b[32m"

/** Terminal reset command. */
const val RESET = "\u001b[0m"
// endregion
