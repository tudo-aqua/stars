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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.root

class TSCDSLTest {
  @Test
  fun `Test the correct building of a TSC in the DSL`() {
    val tsc =
        root<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          bounded("root", 0 to 2) {
            condition { _ -> true }

            projections {
              projection("P1")
              projection("P2")
              projectionRecursive("PR1")
              projectionRecursive("PR2")
            }

            monitors {
              monitor("monitor1") { _ -> true }
              monitor("monitor2") { _ -> true }
              monitor("monitor3") { _ -> true }
              monitor("monitor4") { _ -> true }
            }

            //            optional("optional") {
            //              leaf("leaf_optional")
            //              //leaf("leaf_optional")
            //            }
            //
            //            all("all") { leaf("leaf_all") }
            //
            //            any("any") { leaf("leaf_any") }
            //
            //            exclusive("exclusive") { leaf("leaf_exclusive") }
            //
            //            bounded("bounded", 2 to 3) {
            //              leaf("leaf_bounded") {
            //                // leaf("Leaf_Wrong") {}
            //                monitors{
            //
            //                }
            ////                monitors{
            ////
            ////                }
            //              }
            //
            //              monitors{
            //                //any()
            //                //leaf()
            //                monitor("monitor") { _ -> true }
            //              }
            //            }
            //
            //            leaf("leaf_root") {
            //              // any("any_wrong") { }
            //            }
          }
        }
    tsc
  }
}
