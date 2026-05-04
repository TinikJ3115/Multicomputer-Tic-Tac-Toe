# Technical Report

## Introduction

This project implements a multiplayer Tic Tac Toe game for a local network using Java socket programming. The goal is to allow two players on different computers to connect to a shared server, take turns, and see game updates in real time through a graphical interface.

## Features Description

- Client-server socket communication for all multiplayer actions
- Player connection and game initialization
- Turn-based move handling
- Real-time board synchronization between clients
- Win, lose, and draw result display
- Java Swing user interface for player input

## Implementation Details

The project is organized into five parts:

- `server`: accepts connections, manages both players, validates moves, and broadcasts the latest game state
- `client`: connects to the server, sends moves, receives updates, and forwards them to the UI
- `game`: contains the board, turn flow, and win/draw logic
- `shared`: contains the socket protocol and message formatting used by both client and server
- `ui`: displays the menu, board, status messages, and game result dialogs

The server assigns player symbols `X` and `O`, starts the game when both players connect, and sends updated state messages after each valid move. The clients use those state messages to refresh the board and status display.

## Challenges And Decisions

- A shared message format was needed so the client and server could interpret data consistently.
- The game state had to be centralized on the server so both players always see the same board.
- The UI had to support both a local demo mode and a network game mode.

## Conclusion

The project demonstrates how socket programming can be used to coordinate a turn-based multiplayer game across two computers. It combines networking, game-state management, and a graphical interface into a complete local-network application.
