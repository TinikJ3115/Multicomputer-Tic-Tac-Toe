# Progress Report

## Progress So Far

- Reviewed requirements and constraints (socket-based communication only)
- Planned overall architecture (client-server structure)
- Started implementing basic socket connection
- Began structuring game logic
- Began structuring network connection
- Created shared GitHub repo  
  [TinikJ3115/Multicomputer-Tic-Tac-Toe](https://github.com/TinikJ3115/Multicomputer-Tic-Tac-Toe)
- Added the Java Swing user interface for local and network play
- Connected the server, client, and game logic through a shared socket protocol
- Implemented turn handling, board updates, win/draw detection, and rematch flow
- Tested the current code to make sure it compiles and the basic multiplayer flow works

## Challenges

- Coordinating game state updates between two different clients
- Making sure only the correct player can move on each turn
- Keeping the user interface updated whenever the server sends new game data

## Current Status

The project now has a working Java codebase with socket-based server and client communication, game logic, and a user interface. Players can host or join a match and play Tic Tac Toe through the network.

## Next Steps

- Do final multi-computer testing on the same local network
- Polish the presentation and demo flow
- Prepare final technical and contribution reports
