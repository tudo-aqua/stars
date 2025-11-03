**STARS** is a formal framework for analyzing and measuring _scenario coverage_ in testing automated robotic systems.
It enables users to formally define environment features and classify _recorded_ data into distinct scenario classes 
using _Tree-based Scenario Classifiers_ (TSCs).

By expressing scenario features in temporal logic and combining them hierarchically, STARS automatically identifies 
which scenarios were encountered in recorded data — and which are still missing. Through logically defined monitors, 
it can also validate requirements and provide insights into the simultaneous occurrence of features and requirement violations.

The framework computes metrics such as _scenario coverage_, _feature occurrence_, and _scenario distributions_, supporting 
safety assessment in regards to standards like **ISO 21448** and **UL 4600**.

STARS is domain-agnostic, highly customizable, and implemented in Kotlin for seamless interoperability with JVM-based languages.

Repository: [github.com/tudo-aqua/stars](https://github.com/tudo-aqua/stars)