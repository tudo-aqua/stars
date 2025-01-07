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

package tools.aqua.stars.core.metric.providers

import tools.aqua.stars.core.evaluation.TSCEvaluation

/**
 * Implementing the [Plottable] interface allows the results of a [MetricProvider] to be plotted as
 * a graph.
 */
interface Plottable {
  /**
   * This function is called after the evaluation phase and should plot the data collected during
   * the evaluation.
   *
   * @see TSCEvaluation.runEvaluation
   */
  fun writePlots()

  /**
   * This function is called after the evaluation phase and writes the plot data collected during
   * the evaluation into CSV files.
   *
   * @see TSCEvaluation.runEvaluation
   */
  fun writePlotDataCSV()
}
