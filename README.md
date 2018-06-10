# Resizeable317Client
A client for RS2 rev. 317 with realizable functionality.

# What's Been Done
Basically, as the window changes in size the standard interface always floats to the bottom right hand corner and the viewport draws wherever there is no interface. Mostly all of the listeners for mouse clicks had hard coded values, and to not go through the effort of changing all of these by hand, we change the variables for the mouse events dynamically. So overall, the main accomplishments here are just that the window can be resized and the gameframe will adjust as necessary, and the interfaces/game world are interactable like they would be normally.

# Problems
Currently, the largest problem with the client is performance once you extend the size past what it was intended to be. This is not surprising given how much everything was hardcoded, nothing is flexible. I'm not sure how to fix this performance issue without rewriting how rendering is done.
