# SmartGovLezModel

SmartGovLezModel is a simulation model dedicated to the study of [Low Emission Zones (LEZ)](https://en.wikipedia.org/wiki/Low-emission_zone) in an urban context.
SmartGovLezModel is based on [SmartGovSimulator](https://github.com/smartgov-liris/SmartGovSimulator), a generic agent based simulator dedicated to urban problems.

## General description of the model

A [Low Emission Zones (LEZ)](https://en.wikipedia.org/wiki/Low-emission_zone)
is a special urban area where more polluting vehicles are not allowed to enter.

Perimeters and permissions of such zones is very variable in the European Union.
This simulation model, realized at the [LIRIS lab](https://liris.cnrs.fr/en),
is an attempt to evaluate the impact of such zones on pollutant emissions, in
order to learn optimal policies to help stakeholders.

SmartGovLezModel implements the following **modeling features**:

* Fleet generation from an input establishment set of private agents and delivery agents
* Creation of neighborhoods with different policies in connection with Low Emission Zones
* Vehicle replacements depending on the policy choices
* Agent's satisfaction indicator about the political choices
* Simulation over a day, and pollutant emissions computation with /without the input LEZ, using the [COPERT model](https://www.emisia.com/utilities/copert/).
* Module of multi-agent reinforcement learning to predict optimal policies using 
[Clustered Deep Q Network](https://europe.naverlabs.com/wp-content/uploads/2019/04/Multi-Agent-Learning-and-Coordination-with-Clustered-Deep-Q-Network.pdf)

The final model is provided as a Command Line Interface wrapped in a single .jar file
and takes various input and output parameters. See the
[full documentation](documentation/README.md) for detailed
usage instructions.

## Links to further documentation

* [Building the simulator from sources](./INSTALL.md)
* [Running simulations](documentation/README.md) (usage instructions).
* [Developer's notes and tips](documentation/Developers.md)

## Team Contacts

SmartGov is developed at the [LIRIS lab](https://liris.cnrs.fr/en) within the [SyCoSMA](https://liris.cnrs.fr/equipe/sycosma) Multi-Agent Systems team.

For any extra information concerning the project, you may contact :

* [Veronique Deslandres](https://liris.cnrs.fr/page-membre/veronique-deslandres) : veronique.deslandres@liris.cnrs.fr
* Alban Flandin : alban.flandin@etu.univ-lyon1.fr
