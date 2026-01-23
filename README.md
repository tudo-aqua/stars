# STARS
[![Build](https://github.com/tudo-aqua/stars/actions/workflows/analyze-build-deploy.yml/badge.svg)](https://github.com/tudo-aqua/stars/actions)

[![Project Status: Inactive – The project has reached a stable, usable state but is no longer being actively developed; support/maintenance will be provided as time allows.](https://www.repostatus.org/badges/latest/inactive.svg)](https://www.repostatus.org/#inactive)

[![Maven Central](https://img.shields.io/maven-central/v/tools.aqua/stars-core?logo=apache-maven&label=MavenCentral%20stars-core)](https://central.sonatype.com/artifact/tools.aqua/stars-core)\
[![Maven Central](https://img.shields.io/maven-central/v/tools.aqua/stars-logic-kcmftbl?logo=apache-maven&label=MavenCentral%20stars-logic-kcmftbl)](https://central.sonatype.com/artifact/tools.aqua/stars-logic-kcmftbl)\
[![Maven Central](https://img.shields.io/maven-central/v/tools.aqua/stars-importer-carla?logo=apache-maven&label=MavenCentral%20stars-importer-carla)](https://central.sonatype.com/artifact/tools.aqua/stars-importer-carla)\
[![Maven Central](https://img.shields.io/maven-central/v/tools.aqua/stars-data-av?logo=apache-maven&label=MavenCentral%20stars-data-av)](https://central.sonatype.com/artifact/tools.aqua/stars-data-av)

STARS (Scenario-Based Testing of Automated Robotic Systems) is a formal framework for coverage analysis of test data of automated robotic systems.

See [stars-carla-experiments](https://github.com/tudo-aqua/stars-carla-experiments), or [stars-auna-experiments](https://github.com/tudo-aqua/stars-auna-experiments) for examples on how to use the 
framework.

## Getting Started

### Important note
This is a legacy version of STARS. The latest version of STARS can be found on the main branch of this repository.

### Setup

Start by adding the latest version of STARS as a dependency to your project.

#### Gradle
```gradle
implementation("tools.aqua:stars-core:1.0")
```

#### Maven
```xml
<dependency>
  <groupId>tools.aqua</groupId>
  <artifactId>stars-core</artifactId>
  <version>1.1</version>
</dependency>
``` 

#### (Optional) Git Hooks
If you want to use our proposed Git Hooks you can execute the following command:
```shell
git config --local core.hooksPath .githooks
```

