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

package tools.aqua.stars.importer.carla

import java.nio.file.Path
import tools.aqua.stars.data.av.dataclasses.World

/**
 * Contains the information for all simulation runs for one specific map. Each
 * [CarlaSimulationRunsWrapper] contains a [Path] to the [staticDataFile] and a list of [Path]s for
 * the [dynamicDataFiles]. It also holds properties for calculated [World]s and the [Path]s as an
 * [ArrayDeque] in [dynamicDataFiles]. The [dynamicDataFiles] are sorted by the seed if
 * [sortFilesBySeed] is set to true.
 *
 * @property staticDataFile The [Path] to map data file containing all static information.
 * @param dynamicDataFiles A [List] of [Path]s to the data files which contain the timed state. data
 *   for the simulation
 * @param sortFilesBySeed Whether to sort the [dynamicDataFiles] by the seed.
 */
class CarlaSimulationRunsWrapper(
    val staticDataFile: Path,
    dynamicDataFiles: List<Path>,
    sortFilesBySeed: Boolean = true
) {
  /** Holds the [World] object. */
  val world: World = loadWorld(staticDataFile)

  /** Holds an [ArrayDeque] of [Path]s. */
  val dynamicDataFiles: ArrayDeque<Path> =
      ArrayDeque(
          if (sortFilesBySeed) dynamicDataFiles.sortedBy { getSeed(it.fileName.toString()) }
          else dynamicDataFiles)
}
