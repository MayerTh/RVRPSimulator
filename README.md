# RVRPSimulator
RVRPSimulator is a [discrete event simulator] (https://en.wikipedia.org/wiki/Discrete_event_simulation) for the domain of vehicle routing. It provides a simulation model, which supports modelling various vehicle routing problem instances (also known as multi-constraint or rich vehicle routing problems). The RVRPSimulator model is classified after a recent Rich Vehicle Routing Problem taxonomy from [Lahyani et al. (2015)] (https://www.researchgate.net/publication/267308629_Rich_vehicle_routing_problems_From_a_taxonomy_to_a_definition). 
Another big benefit of the RVRPSimulator model is the support of **dynamic** and **stochastic** vehicle routing problems. Details about the classification are coming soon. 

Beside the simulation model, RVRPSimulator provides the simulation engine (with visualization), which is able to execute the model and create statistics to analyse/evaluate the execution. The RVRPSimulator engine and model also provides interfaces for dynamic vehicle routing, so you can easily evaluate/test your routing dispatching strategies.

**But why we need to simulate vehicle routing problem solutions?** The answer is easy: real world routing problems are stochastic in nature and some of them are even dynamic. So how good is my static routing solution if the customer demands, travelling times (and many more) are varying? And how good is my developed dispatching strategy for dynamic demands? Simulation can answer these questions. Following figure shows how to integrate RVRPSimulator into the VRP solution finding and evaluating process. RVRPSimulator provides powerful interfaces to connect different optimization algorithms regarding solving VRP's or managing dynamic VRP's. 

![RVRPSimulator integrated into the VRP solution finding process] (https://raw.githubusercontent.com/MayerTh/RVRPSimulator/master/vrpsim-core/abstract-model/abstract_model.png)

## Documentation
You can access the full documentation in the [wiki] (https://github.com/MayerTh/RVRPSimulator/wiki), the documentation includes a modelling guide, how to plugin static and dynamic optimizer, how to setup a simulation, how to use the visualization, and much more.

## Open issues and features
* Recording of simulation statistic for evaluation
* Pre implemented dynamic or static optimization algorithms
* Integration of existing static optimizer (e.g. [jsprit] (https://github.com/graphhopper/jsprit))
* ...

## Contact
For any questions or help, feel free to contact [me] (mailto:Thomas.Mayer@unibw.de), or communicate bugs or features requests with the help of the [issue tracker] (https://github.com/MayerTh/RVRPSimulator/issues). Any kind of feedback is always welcome.

## License
This software is released under the [Apache License Version 2.0] (https://github.com/MayerTh/RVRPSimulator/blob/master/LICENSE).