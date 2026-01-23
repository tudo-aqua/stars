/*
 * Copyright 2025-2026 The STARS Project Authors
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

/** Computes the binomial coefficient "n choose k". */
fun binomial(n: Int, k: Int): Long {
  var k = k
  if (2 * k > n) k = n - k

  var b: Long = 1
  for (i in 1..k) {
    b = b * (n + 1 - i) / i
  }
  return b
}
