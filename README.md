# Multicomputer-Tic-Tac-Toe
Operating System 439 Project - Multicomputer Tic Tac Toe

This project is a socket-based multiplayer Tic Tac Toe game written in Java. It supports:

- local demo mode for quick testing
- two-player socket gameplay across multiple computers on the same network
- a Java Swing interface for player input and game updates
- real-time board synchronization through the server

## Project Structure

- `src/server`: server-side socket handling and game coordination
- `src/client`: client networking and startup
- `src/game`: game rules, board state, turn flow, and win/draw logic
- `src/ui`: Java Swing user interface
- `src/shared`: shared protocol and message classes

## How To Run

### Option 1: Double-click launcher

- macOS: `Launch Tic Tac Toe Mac.command`
- Windows: `Launch Tic Tac Toe Windows.bat`

This opens the main menu where you can either start a local demo or host/join a network game.

### Option 2: Terminal

Compile:

```bash
javac -d out $(find src -name '*.java')
```

Open the client menu:

```bash
java -cp out ui.MainMenu
```

Run the server directly:

```bash
java -cp out server.ServerMain
```

## Network Play

### Host and Join

1. Open the game on the host computer.
2. Click `Host and Join`.
3. Share the host computer's local IP address with the second player.
4. On the second computer, open the game, enter the host IP, and click `Join Server`.

### Join Existing Server

1. Make sure the server is already running.
2. Enter your name, server host, and port.
3. Click `Join Server`.

## Features

- socket-based communication between players
- multiplayer gameplay across two computers
- game initialization and turn-based flow
- real-time board updates to all connected clients
- win, lose, and draw result display
- graphical user interface for player interaction
