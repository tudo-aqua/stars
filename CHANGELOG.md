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

## [0.2.1] - 17.11.2023
### Added
- Add git pre-commit hook for `spotlessCheck`
- Add `orderFilesBySeed` flag to the `loadSegments()` function which loads the `AVDataClasses`
- Add logging for `AverageVehiclesInEgoBlockMetric`
- Add additional scaled plots `validTSCInstanceOccurrencesPerProjection_scaled` and `validTSCInstancesProgressionPerProjection_combined_percentage_scaled`

## Changed
- Ignore empty data sets when creating plots (i.e. do not create plot)
- Update legend entry for `validTscInstancesOccurrences` to also include (occurred/total) instances

## Fixed
- Set `egoVehicle` value according to actual value of given Actor
- Fix `sliceRunIntoSegments()` by correctly using the `minSegmentTickCount` attribute
- The logger with level `FINEST` is now using the correct formatter
- Fix differing analysis result directories for plots, CSV files and logs 

## Removed
- Remove `AverageVehiclesInEgoBlockMetric` `println()` statement

## [0.2] - 03.11.2023
### Added
- Add `Plottable` interface
- Add new `DataPlotter` with PNG and CSV export functionalities
- Add missing KDoc documentation for metric classes
- Add plot export to `ValidTSCInstancesPerProjectionMetric`
- Add CSV export to `ValidTSCInstancesPerProjectionMetric`

## Changed
- Update `lets-plot` dependency version

## Fixed
- Fix failing pipeline by introducing `codecov.yml` specification
- Rename package and Maven artifact `tools.aqua.stars.import.carla` to `tools.aqua.stars.importer.carla` due to Java naming conventions

## Removed
- Remove old `DataPlotter`

## [0.1] - 01.09.2023
First release of the STARS framework.