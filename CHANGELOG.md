[0.5]: https://github.com/tudo-aqua/stars/releases/tag/v0.5
[0.4]: https://github.com/tudo-aqua/stars/releases/tag/v0.4
[0.3]: https://github.com/tudo-aqua/stars/releases/tag/v0.3
[0.2.2]: https://github.com/tudo-aqua/stars/releases/tag/v0.2.2
[0.2.1]: https://github.com/tudo-aqua/stars/releases/tag/v0.2.1
[0.2]: https://github.com/tudo-aqua/stars/releases/tag/v0.2
[0.1]: https://github.com/tudo-aqua/stars/releases/tag/v0.1
<!-- ### Added -->
<!-- ### Changed -->
<!-- ### Fixed -->
<!-- ### Removed -->
<!-- ### Security -->
<!-- ### Deprecated -->

# Changelog
All notable changes to this project will be documented in this file.

## [0.5] - 06.11.2024
### Added
- Add `PreEvaluationHooks` before evaluation of `TSCs` and `Segments` in `TSCEvaluation`.
- Add pre-defined `MinNodesInTSCHook` and `MinTicksPerSegmentHook`.
- Add `identifier` field to `TSC`.
- Add `euclideanDistance` function to `Location`.
- Add `vehicleType` field to `Vehicle`.
- Add `loggerIdentifier` field to `Loggable` interface.
- Add `Serializable` interface.
  - This adds the functionality to compare your current analysis results with the previous run or a specified baseline. 
- Add experiment run metadata file containing the experiment run configuration and system information.

### Changed
- `TSCEvaluation` now accepts multiple `TSCs` instead of `TSCProjections`.
- `registerMetricProviders` now throws an `IllegalArgumentException` when multiple instances of the same `MetricProvider` class is registered.
- Root nodes in a `TSC` now **must not** have a condition.
- Move `label` from `TSCEdge` to `TSCNode`.
- All default metrics now implement the new `Serializable` interface.
- Rename `ProjectionMetricProvider` to `TSCMetricProvider`.
- Rename `ProjectionAndTSCInstanceNodeMetricProvider` to `TSCAndTSCInstanceNodeMetricProvider`.
- Rename `InvalidTSCInstancesPerProjectionMetric` to `InvalidTSCInstancesPerTSCMetric`.
- Rename `ValidTSCInstancesPerProjectionMetric` to `ValidTSCInstancesPerTSCMetric`.
- Rename `MissedTSCInstancesPerProjectionMetric` to `MissedTSCInstancesPerTSCMetric`.
- Rename `MissingPredicateCombinationsPerProjectionMetric` to `MissingPredicateCombinationsPerTSCMetric`.
- Rename `DataSaver` to `PlotDataSaver`.

### Fixed
- Fix `toString()` function of `TSCNode` to include the root node's label.

### Removed
- Remove `TSCProjection` class. Now, for each projection in a `TSC` a new `TSC` is created and evaluated.

## [0.4] - 02.08.2024
### Added
- Add iterator for `TSC`.
- Add support for multiple monitors per node.
- Add dedicated `TSCBuilders` for bounded nodes and leaf nodes.
- Add dedicated `TSCBuilders` for monitors and projections.
- Add dedicated `TSCBuilders` for conditions and valueFunction.
- Add check for duplicated `TSCNode` labels.

## [0.3] - 16.05.2024
### Added
- Add generic `TickUnit` and `TickDifference` instead of `Double` identifier. 
  - **Note**: These two types add to the three existing base types (``SegmentType``, `EntityType` and `TickDataType`) and are now required to use the STARS framework.
- Add ``TickDataUnitMilliseconds`` class which implements the newly introduced `TickUnit` interface.
- Add ``TickDataDifferenceMilliseconds`` class which implements the newly introduced `TickDifference` interface.
- Add ``registerMetricProviders`` in ``TSCEvaluation``.
- Add ``TickDataUnitSeconds`` and ``TickDataDifferenceSeconds`` classes for `tools.aqua.stars.data.av.dataclasses` package.
- Add options to skip creation of CSVs and plots via ``writeCSV`` and ``writePlots`` parameters in ``TSCEvaluation.runEvaluation()``.
- Add TSC instance of failing monitor to ``TSCMonitorResult``.
- Add ``onlyMonitor`` flag to ``TSCNode`` and corresponding DSL function for global monitors.
- Add ``plotDataAsHistogram()`` function to ``DataPlotter``.
- Add ``size`` and ``logscale`` parameter to all plotting functions in ``DataPlotter``.
- Add ``FailedMonitorsGroupedByTSCInstanceMetric`` to track all failed monitors and group the results by TSC instances.
- Add ``FailedMonitorsGroupedByTSCNodeMetric`` to track all failed monitors and group the results by TSC nodes.

### Changed
- Rename ``NullaryPredicate.evaluate()`` function to ``holds()`` to match naming conventions of other predicates.
- Rename ``PredicateContext.evaluate()`` function to ``holds()`` to match naming conventions of other predicates.
- Rename ``PostEvaluationMetricProvider.evaluate()`` function to ``postEvaluate()`` to distinguish it from functions from `EvaluationMetricProvider`.
- Rename ``PostEvaluationMetricProvider.print()`` function to ``printPostEvaluationResult()`` to distinguish it from functions from `EvaluationMetricProvider`.
- Replace ``SegmentDurationPerIdentifierMetric`` with ``TotalSegmentTickDifferencePerIdentifierMetric``.
- Replace ``TotalSegmentTimeLengthMetric`` with ``TotalSegmentTickDifferenceMetric``.
- Replace ``tickData`` field in SegmentType with by getter on ``ticks``.

### Fixed
- Range checks in CMFTBL operators

### Updated
- Clarify documentation and added missing documentation at various instances.
- Replace domain keywords (i.e. ``actor``, ``egoVehicle``, etc.) with generic variants at several places.
- Correct order of parameters for ``BinaryPredicate`` constructor to match other predicates.
- Updated detekt config for _FunctionNaming_. New checked rule is: ``([a-z][a-zA-Z0-9]*)|(\`[a-zA-Z0-9 ,.-]+\`)``.

### Removed
- Remove field ``SegmentType.tickIDs``. Use ``SegmentType.ticks.keys`` call instead.
- Remove field ``SegmentType.firstTickId``. Use ``SegmentType.ticks.keys.first()`` call instead.
- Remove field ``PredicateContext.tIDs``. Use ``PredicateContext.segment.ticks.keys`` call instead.

## [0.2.2] - 13.02.2024
### Added
- Add missing documentation
- Add missing `previous` CMFTBL operator using two entities
- Add additional sanity checks for the `primaryEntityId` in `Segments`
- Add support for plotting and saving specific `x` and `y` values

### Changed
- Change Kotlin version from `1.7.10` to `1.9.10`
- Introduce new subpackages and move files accordingly
- Use `jvmToolchains`
- Split plotting and writing of CSV files into two separate functions

### Fixed
- When using `useEveryVehicleAsEgo` the existing flags are now correctly reset
- Add missing `evaluate` function call of `PostEvaluationMetricProvider`

### Security
- Update project to Java 17
- Update lets-plot library to fix security issue

## [0.2.1] - 17.11.2023
### Added
- Add git pre-commit hook for `spotlessCheck`
- Add `orderFilesBySeed` flag to the `loadSegments()` function which loads the `AVDataClasses`
- Add logging for `AverageVehiclesInEgoBlockMetric`
- Add additional scaled plots `validTSCInstanceOccurrencesPerProjection_scaled` and `validTSCInstancesProgressionPerProjection_combined_percentage_scaled`

### Changed
- Ignore empty data sets when creating plots (i.e. do not create plot)
- Update legend entry for `validTscInstancesOccurrences` to also include (occurred/total) instances

### Fixed
- Set `egoVehicle` value according to actual value of given Actor
- Fix `sliceRunIntoSegments()` by correctly using the `minSegmentTickCount` attribute
- The logger with level `FINEST` is now using the correct formatter
- Fix differing analysis result directories for plots, CSV files and logs 

### Removed
- Remove `AverageVehiclesInEgoBlockMetric` `println()` statement

## [0.2] - 03.11.2023
### Added
- Add `Plottable` interface
- Add new `DataPlotter` with PNG and CSV export functionalities
- Add missing KDoc documentation for metric classes
- Add plot export to `ValidTSCInstancesPerProjectionMetric`
- Add CSV export to `ValidTSCInstancesPerProjectionMetric`

### Changed
- Update `lets-plot` dependency version

### Fixed
- Fix failing pipeline by introducing `codecov.yml` specification
- Rename package and Maven artifact `tools.aqua.stars.import.carla` to `tools.aqua.stars.importer.carla` due to Java naming conventions

### Removed
- Remove old `DataPlotter`

## [0.1] - 01.09.2023
First release of the STARS framework.