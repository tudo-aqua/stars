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

package tools.aqua.stars.core.metric.providers

import tools.aqua.stars.core.metric.serialization.SerializableResult

/** This interface should be implemented when a metric has a running state. */
interface Stateful {
  /**
   * This function returns the current state of the implementing class.
   *
   * @return The current state.
   */
  fun getState(): Any?

  /** This function prints the current state of the implementing class. */
  fun printState()

  fun getSerializableResults(): SerializableResult

  fun compareResults(otherResult: SerializableResult): Boolean {
    if (this.getSerializableResults().javaClass.name != otherResult.javaClass.name) {
      throw RuntimeException("These results cannot be compared")
    }
    return this.getSerializableResults() == otherResult
  }
}
