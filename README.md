# panikabor-3000
Panikabor 3000 is a multi-agent system which simulate a evacuation from a burning room. 
The result of the simulation depend on severals parameter such as the number of people, 
the number of exits, the number of obstacles or the proportion of behavior with the generated population of agent

## Make it work
### Compile

** Inside a terminal ** :
```bash
javac -cp lib/jade.jar:lib/mason.19.jar:lib/jcommon-1.0.23.jar:lib/jfreechart-1.0.19.jar:lib/javafx.base.jar src/gui/* src/main/* src/modele/jade/*.java src/modele/*.java src/modele/pathfinding/*.java -d bin/
```
Now you should have the `bin` folder with all the .class files within.

Before running the compiled sources, you **NEED** to do 2 extra-steps :

Copy the `main.properties` file to the `bin` folder
`cp src/modele/jade/main.properties bin/modele/jade/main.properties`

Copy the `environnement.properties` file to the `bin` folder
`cp src/modele/jade/environnement.properties bin/modele/jade/environnement.properties`

Now we can start the famous panikabor-3000 :)

### Running the thing

**Inside a terminal**
```bash
java -cp bin/:lib/jade.jar:lib/mason.19.jar:lib/jfreechart-1.0.19.jar:lib/javafx.base.jar:lib/jcommon-1.0.23.jar main.Main 
```

And a window should appear, well done you have started panikabor-3000 !
![What you should get after the command](https://i.imgur.com/bPjbCYD.png)
This is what you should get at the end.

Press the Pause symbol on the smallest window to initialize the simulation and press the Run symbol to act 1 round.

Press the Pause symbol again to make the simulation run continuously

## Simulation customization

You can adjust the settings of the simulation by modifying the `src/modele/Constantes.java` file and change the values of the variables. Read the explanations if you want to know more about them :)

Have some fun with them, it can be entertaining to watch 

## Some explanations

An agent can do :
* Search the nearest exit
* Move
* Fall on the ground
* Help an other agent on the ground
* Push an other agent
* Extinguish an other agent
* Die

An agent have :
* HP
* Scope of vision
* Get Up probability
* Behavior

There is three avaible behaviors :
* Hero
* Selfish
* Scared

The **HERO** will walk on fire because he can, he won't push others to pass, he will help other agents and warn everyone.
The **SELFISH** won't walk on the fire, will push others, won't help anyone and won't warns others to gain the exit quicker.
The **SCARED** won't walk on fire, will push others, won't help but will warns everyone by its screams of terror.

An agent can lost HP by :
* Being walked on
* Catching fire

By dying, an agent become an inanimate object called a corpse. A corpse occupy the same place on a case as a agent but won't move.

If a case is filled by 3 agents or corpses or a combinaison of the two no one can go into that case.

Filling a case or quitting a filled case can make an agent fall as well as being pushed by an other agent.

In the fell state, the agent can't move until someone help him. However, he will try to get up whenever there is no other agent on the case.

If an other agent passes through a case where there is an agent in the fell state, the agent on the ground will lose HP.

Exits can be blocked by :
* Corpses
* Fire
* Fell agents (the exit won't unblock if they get up, which is a known bug)

A blocked exit won't be usable and agents will try to locate the nearest exit within their filed of view and their memory (every case they looked and attainable).

If an agent can't locate a valid exit, they will to go to the nearest unknown area of the room to find an exit.

If all the exits are blocked, agents will immedialty know it and will stop moving, awaiting death. This is antoher weird and funny known bug.

The fire do things too :
* It try to expends in every case around him at every turn
* It can become extinguish if a fire is around 4 fires
* It die after a defined amount of time

## Screenshots

Turn 0 - Initialization
![Turn 0](https://i.imgur.com/7RumTN3.png)

Turn 5 - The fire expends, two agents have seen the fire and enters alert mode, they try to escape to the nearest exit and will warn about the fire to agents around
![Turn 5](https://i.imgur.com/N1DmSl7.png)

Turn 20 - The great escape. The first two agents have warned two others agents who are too far away to see the fire. They enter alert mode too
![Turn 20](https://i.imgur.com/lX5OlcJ.png)

Turn 60 - The first death. While trying to reach the north exit, an agent caught fire and died, his corpse making an obstacle for every other agent, which placed in the wrong place, can block exits
![Turn 60](https://i.imgur.com/h9FcKjH.png)

