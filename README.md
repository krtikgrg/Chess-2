## Overview
This is a Java Applet created as the final project for AP Computer Science A during my high school course. The assignment was to create a functional 2 player chess game that followed the legal moves of chess. After completing the requirements for the assignment quite early on before it was due, I decided to step up the project and create a simple AI that I could play against in an optional 1-player mode.

As such, this project turned into more of a proof of concept/research into basic AI concepts. The AI uses a recursive function to determine a path of moves (to a certain depth) that benefit it most in the end using a move evaluation function that accounts for captures based on piece value, as well as pawn structure, and overall material balance.

Due to lack of time before the deadline of the final, the AI plays at a somewhat competent level, but does have bugs that could be fixed after revision.

![Applet View](https://github.com/sashaouellet/chess/blob/master/ChessApplet.png "Chess Applet Example View")

## Use
Compile and run via `appletviewer` in the console

Switching between single and 2 player mode can be changed in the .html file's single player boolean property.

