**STARS** is a formal framework for analyzing and measuring _scenario coverage_ in testing automated robotic systems. 
It allows users to formally define (environment) features and classify _recorded_ data into distinct scenario classes 
using _Tree-based Scenario Classifiers_ (TSCs).

By expressing scenario features in temporal logic and combining them hierarchically, STARS automatically identifies which 
scenarios were encountered in recorded data — and which are still missing. 
It computes metrics such as coverage, feature occurrence, and scenario distributions, supporting data-driven test selection 
and safety assessment in accordance with standards like **ISO 21448** and **UL 4600**.

STARS is domain-agnostic, highly customizable, and implemented in Kotlin for interoperability with JVM-based languages.

Repository: [github.com/tudo-aqua/stars](https://github.com/tudo-aqua/stars)