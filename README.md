# Simplification of scenarios for Autonomous Driving Systems

## Structure of the repository

This repository contains the code of the ADS scenario simplifier, as described in the paper "Less is More: Simplification of Test Scenarios for Autonomous Driving System Testing"

The code can be extended to different ADS simulators, by extending the classes:
* *ads/ADSScenario.java*: it specifies the format for scenarios of the considered ADS simulator
* *ads/ADSRunner.java*: it provides an interface with the ADS simulator, to specify how to run scenarios
* *results/ResultsAndLoader.java*: it specifies how to load scenarios of the considered ADS
* *ads/testMeasure/TestMeasure.java*: it specifies the test measure of interest

## People
* Paolo Arcaini http://group-mmm.org/~arcaini/
* Xiao-Yi Zhang https://group-mmm.org/~xiaoyi/
* Fuyuki Ishikawa http://research.nii.ac.jp/~f-ishikawa/en/

## Paper
P. Arcaini, X. Zhang, F. Ishikawa. "Less is More: Simplification of Test Scenarios for Autonomous Driving System Testing", in Proceedings of the 15th International Conference on Software Testing, Validation and Verification (ICST 2022)
