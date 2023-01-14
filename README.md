# netgame

This is a multiplayer game project created for Distributed Computing class.

The game being played is a bluffing variant of Rock, Paper, Scissors. Each player declares the move
they are going to do. It is up to the player if they want to follow up on the declared move or change
their move in response to the opponent's intent.

## Instructions

### Running the game

1. Download the latest release of the game from the releases page
2. Extract the contents of the ```.zip``` file
3. Run the included ```.jar``` file

### Running the game in CLI mode

1. Download the latest release of the game from the releases page
2. Extract the contents of the ```.zip``` file
3. Change the third value of the ```.config``` file in a text editor from  ```true``` to ```false```
4. Run the included ```.jar``` file

### Hosting your own server instance

1. Download the latest release of the game from the releases page
2. Extract the contents of the ```.zip``` file
3. Run the included ```.jar``` file and pass the port number for the game connections and the port number for the monitoring service
  - Example: ```java -jar netgame.jar 8080 8000```

### Connecting to another server

1. Open the ```.config``` file in a text editor
2. Change the first value to the IP address of the server
  - If hosting locally, simply change to ```127.0.0.1```
3. Change the second value to the port number opened by the server
4. Run the ```.jar``` file to play on that server
