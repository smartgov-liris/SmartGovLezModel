# SmartGovLezModel

A Low Emission Zones model implementation for the
[SmartGovSimulator](https://github.com/smartgov-liris/SmartGovSimulator).

# Documentation
The project is documented in detail [here](documentation/README.md).

# Introduction

A Low Emission Zone (LEZ) is a special urban area where more polluting vehicles are
not allowed to enter.

Perimeters and permissions of such zones is very variable in the European
Union, so this project from the [LIRIS](https://liris.cnrs.fr/en) is an attempt
to evaluate the impact of such zones on pollutant emissions, and to learn optimal policies
to help stakeholders.

# Features

The following features have been implemented :
- Fleet generation from an input establishment set of private agents and delivery agents
- Creation of neighborhoods with different policies in connection with Low Emission Zones
- Vehicle replacements depending on the polical choices
- Agent's satisfaction indicator about the political choices
- Simulation over a day, and pollutant emissions computation with /
	without the input LEZ, using the [COPERT
	model](https://www.emisia.com/utilities/copert/).
- Module of multi-agent reinforcement learning to predict optimal policies using 
[Clustered Deep Q Network](https://europe.naverlabs.com/wp-content/uploads/2019/04/Multi-Agent-Learning-and-Coordination-with-Clustered-Deep-Q-Network.pdf)

The final model is provided as a Command Line Interface wrapped in a single .jar file
and takes various input and output parameters. See the
[full documentation](documentation/README.md) for detailed
usage instructions.

# Build from source

From the repository where you want to install the source code, run :
`git clone https://github.com/AlbanFl/SmartGovLezModel`

`cd SmartGovSimulator`

## Command line build

To build the project using the [Gradle CLI](https://docs.gradle.org/current/userguide/command_line_interface.html), run :

- `./gradlew build` (UNIX)
- `gradlew.bat build` (Windows)

This will compile the Java classes, and run all the unit tests.

Also :
- a simple .jar file of the project classes is built in the `build/libs` subfolder
- a [Shadow runnable
	.jar](https://imperceptiblethoughts.com/shadow/introduction/) is built at
	the root of the project (`SmartGovLez-MASTER.jar`)
See the
[full documentation](documentation/README.md) for
usage instructions.

## IntelliJ IDEA

To import the project in the IntelliJ IDEA :

`File` -> `New` -> `Project from Existing Sources` (or `Module from Existing Sources`) -> select the `SmartGovLezModel` folder -> `Import project from external model` -> select `Gradle` -> `Finish`

## Eclipse IDE

To import the project in the Eclipse Java IDE :

`File` -> `Import...` -> `Gradle` -> `Existing Gradle Project` ->  select the `SmartGovLezModel` folder -> `Finish`<Paste> 

## Learning modules

The `PoliticRun` task of the project is using python libraries to learn relevant policies.
To launch this task, it is necessary to have :
- Python
- Tensorflow
- Keras



# Contacts

SmartGov is developped at the [LIRIS](https://liris.cnrs.fr/en) within the Multi-Agent System team.

For any extra information about the project, you may contact :
- Veronique Deslandres : veronique.deslandres@liris.cnrs.fr
- Alban Flandin : alban.flandin@etu.univ-lyon1.fr
