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

package tools.aqua.stars.importer.carla

/*
 * List of all vehicle type ids present in carla.
 *
 * @see: https://carla.readthedocs.io/en/latest/catalogue_vehicles/
 */

/** List for base type: CAR. */
val cars =
    listOf(
        "vehicle.audi.a2",
        "vehicle.audi.etron",
        "vehicle.audi.tt",
        "vehicle.bmw.grandtourer",
        "vehicle.chevrolet.impala",
        "vehicle.citroen.c3",
        "vehicle.dodge.charger_2020",
        "vehicle.dodge.charger_police",
        "vehicle.dodge.charger_police_2020",
        "vehicle.ford.crown",
        "vehicle.ford.mustang",
        "vehicle.jeep.wrangler_rubicon",
        "vehicle.lincoln.mkz_2017",
        "vehicle.lincoln.mkz_2020",
        "vehicle.mercedes.coupe",
        "vehicle.mercedes.coupe_2020",
        "vehicle.micro.microlino",
        "vehicle.mini.cooper_s",
        "vehicle.mini.cooper_s_2021",
        "vehicle.nissan.micra",
        "vehicle.nissan.patrol",
        "vehicle.nissan.patrol_2021",
        "vehicle.seat.leon",
        "vehicle.tesla.model3",
        "vehicle.toyota.prius")

/** List for base type: TRUCK. */
val trucks =
    listOf(
        "vehicle.carlamotors.carlacola",
        "vehicle.carlamotors.european_hgv",
        "vehicle.carlamotors.firetruck",
        "vehicle.tesla.cybertruck")

/** List for base type: VAN. */
val vans =
    listOf(
        "vehicle.ford.ambulance",
        "vehicle.mercedes.sprinter",
        "vehicle.volkswagen.t2",
        "vehicle.volkswagen.t2_2021")

/** List for base type: BUS. */
val buses = listOf("vehicle.mitsubishi.fusorosa")

/** List for base type: MOTORCYCLE. */
val motorcycles =
    listOf(
        "vehicle.harley-davidson.low_rider",
        "vehicle.kawasaki.ninja",
        "vehicle.vespa.zx125",
        "vehicle.yamaha.yzf")

/** List for base type: BICYCLE. */
val bicycles =
    listOf("vehicle.bh.crossbike", "vehicle.diamondback.century", "vehicle.gazelle.omafiets")
