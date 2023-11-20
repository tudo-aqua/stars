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

package tools.aqua.stars.data.av.dataclasses

data class Vector3D(val x: Double, val y: Double, val z: Double) {
  constructor(vector: Vector3D) : this(vector.x, vector.y, vector.z)
  constructor(location: Location) : this(location.x, location.y, location.z)

  operator fun minus(other: Vector3D) =
      Vector3D(x = this.x - other.x, y = this.y - other.y, z = this.z - other.z)

  operator fun div(scalar: Number) =
      Vector3D(
          x = this.x / scalar.toDouble(),
          y = this.y / scalar.toDouble(),
          z = this.z / scalar.toDouble())
}
