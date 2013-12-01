jorro: giving water to Acacia and Lily
=====

The jorro is the tool which visualizes and simulates transition system created by Acacia+ and Lily.


Realizablity checkers such as [Acacia+](http://lit2.ulb.ac.be/acaciaplus/) and [Lily](http://www.iaik.tugraz.at/content/research/design_verification/lily/) automatically create transition system from specification written in LTL(Linear Temporal Logic).
But in practical use, the output transition system is too large to understand.
So I present the tool to help understand such output by visualizing and simulating.


### Features
 - jorro can treat **large** transition system.

   jorro does not render all states but only visitted states on-the-fly.

 - you can **configure** how jorro visualize and simulate transition system easily.

   give JSON config file, you can change shape and color of states and select show or hide each infomation.
   See sample/elevator/elevator.config.



### Build
jorro use [Graphviz](http://www.graphviz.org/) to visualize transition system.

1. So at first

  On Ubuntu `sudo apt-get install graphviz`

  On Mac OS X and homebrew `brew install graphviz`

2. Then
```
git clone https://github.com/cloverrose/jorro.git
cd jorro
./setup.sh
```


### Usage
jorro depends some libraries, so classpath is long.
For convenience, I use shell script run.sh.

```
./run.sh sample/elevator/elevator.txt
```

argument is transition system file created by Acacia+ or Lily.


And you can specify some options
 - -c config file (JSON format)

 - -o output file, if does not specify jorro outputs dot file in the same directory of transition system file (e.g. sample/elevator/elevator.dot)

```
./run.sh sample/elevator/elevator.txt -c sample/elevator/elevator.config -o sample/elevator/out.dot
```
