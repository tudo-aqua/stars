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

package tools.aqua.stars.importer.carla

import java.nio.file.Path
import tools.aqua.stars.data.av.dataclasses.Block

/**
 * Contains the information for all simulation runs for one specific map. Each
 * [CarlaSimulationRunsWrapper] contains a [Path] to the [mapDataFile] and a list of [Path]s for the
 * [dynamicDataFiles]. It also holds properties for calculated [Block]s and the [Path]s as an
 * [ArrayDeque] in [dynamicDataFilesArrayDeque]
 *
 * @property mapDataFile The [Path] to map data file containing all static information
 * @property dynamicDataFiles A [List] of [Path]s to the data files which contain the timed state
 *   data for the simulation
 */
data class CarlaSimulationRunsWrapper(val mapDataFile: Path, val dynamicDataFiles: List<Path>) {
  /** Holds a [List] of [Block]s. */
  var blocks: List<Block> = emptyList()
  /** Holds an [ArrayDeque] of [Path]s. */
  val dynamicDataFilesArrayDeque: ArrayDeque<Path> = ArrayDeque(dynamicDataFiles)
}
