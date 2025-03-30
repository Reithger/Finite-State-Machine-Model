# Finite State Machine Model
 
## How do I use this?
 - It's not done yet, but can be used for making some Finite State Machines and doing basic operations on them, so make sure you have GraphViz downloaded so that, on start-up, you can provide the directory to graphviz/bin/dot.exe for the program to be able to make its diagrams.
 - To run this program, find the 'Finite State Machine Model.jar' file and run it (it's basically an .exe); this will ask you to configure its graphviz reference and make some folders to hold the FSMs you generate.
 - The program is split into two halves, the left being to manipulate the currently selected FSM and the right being to display the current affected FSM, allowing you to navigate around it and zoom in. Graphviz generates images, so there is not currently touch-interactivity, I have been thinking about how to do that and fearing the day I start trying.
 - There are three pages for manipulations, the first being to simply edit or generate the current FSM, the second to perform some simple manipulations (product, other ones), and the third page being for experimental UStructure generation and analysis. It's a research program implementing a body of theory that usually stays theory, so lot's of experimental stuff here but hopefully this'll be very useful once it's done.
 - That's about it, I'm planning on writing specific tutorials for each page but a lot of other development goals are gonna come first to make it something that people *want* to use before making it something that's *nice* to use; hopefully that interim period is very short.

This program exists as a research tool, so what you can do with the UI will often be pretty far behind what is implemented for use with specific test environments I run from inside my IDE. For the most up-to-date examples of what the program can do, check the file src.test.datagathering.DataGathering.java which is the central hub of my test configurations, with many of the specified systems of automata that are tested on being defined in the src.test.help package. It's likely very dense for others to use as it's been shaped around my specific needs for it, but I've tried to make it (once you get the flow of it) easy to use; feel free to contact me with any questions you have.

## The Messy, Messy UML Class Diagram

![FSM Model - March 30](https://github.com/user-attachments/assets/3f3f5d9d-6a92-4938-8652-0c0280b5ffea)

Oof, looking back on old work is always rough. Some parts are pretty clean but TransitionSystem is just so, so overloaded with dependencies.
