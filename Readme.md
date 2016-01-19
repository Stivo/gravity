### Gravity simulation

Simulates random planets or the solar system on a 2d plane.
Inspired by [this one](http://justfound.co/gravity/) posted on reddit.

### Running it

Use sbt run.

### Options, Command line

None so far.

### Why?

Just for fun.

Also to see how much faster I can do it in Java than the Javascript one. Currently it takes around 50ms to compute gravities between 1000 stars with parallel colletions.

### Libs used

Scalatest, squants.
Squants made it easy to get the formulas and the units right.
But for performance the typed units have to be replaced with doubles.
