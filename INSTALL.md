# Building the SmartGovLezModel simulator from sources

As a general note, SmartGovLezModel can 
[run on docker](https://github.com/smartgov-liris/SmartGov-docker/tree/master/SmartGovLezModel)
and its full/detailed compiling instructions are
[thus available (Dockerfile)](https://github.com/smartgov-liris/SmartGov-docker/blob/master/SmartGovLezModel/Context/dockerfile).

## Requirements

Building the simulator from sources will require that you first install the
following tools/libraries

* a [git client](https://en.wikipedia.org/wiki/Git#Implementations)
* [openjdk 8](https://openjdk.java.net/install/)
* the [Osmosis](https://github.com/openstreetmap/osmosis) tool

## Building the simulator

First, clone this repository where you want to install the source code:

```bash
git clone https://github.com/smartgov-liris/SmartGovLezModel
cd SmartGovLezModel
```

Then build the java project with the help of [Gradle CLI](https://docs.gradle.org/current/userguide/command_line_interface.html):

* Unix: `./gradlew build`
* Windows: `gradlew.bat build`

The above command will

* compile the Java classes,
* build a simple `.jar` java archive file of the project classes that will
   be placed in the `build/libs` sub-directory
* build `SmartGovLez-MASTER.jar`, a [shadow runnable archive](https://imperceptiblethoughts.com/shadow/introduction/), that will be placed at the
 root of the repository.
* run all the unit tests.

## Building the learning module

The [`PoliticRun` task](documentation/README.md#politic-run) of the SmartGovLezModel, that learns relevant policies, uses [Python](https://www.python.org/) and the following python libraries (as documented in the [requirements](../extsrc/requirements.txt))

* Tensorflow
* Keras
