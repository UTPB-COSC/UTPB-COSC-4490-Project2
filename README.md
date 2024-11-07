# Pirate Battleship Game (COSC 4490 - Project 2, Aswin)

## Overview

Welcome to **Pirate Battleship**, a 2D game where you control a pirate ship navigating treacherous seas filled with obstacles like rocks and enemy threats. The objective is to safely guide your ship while avoiding collisions with rocks and enemy attacks. A collision with a rock or an enemy attack results in game over. However, you can restart and try again by pressing the "R" key.

Built in Java with Swing for graphics, this game includes custom assets for the pirate ship, rocks, enemies, and sea background, along with sound effects and a debug mode for development insights.

## Features

- **Title Screen**: A welcome screen with a "Start Game" button to begin and an overview of controls.
- **Pause Menu**: A menu allowing players to pause, resume, reset, or quit the game.
- **Boat Navigation**: Use the arrow keys to control the pirate ship’s movement.
- **Obstacle Collision**: Game ends if the ship collides with a rock or an enemy.
- **Enemy Boat**: AI-controlled enemy boat that patrols a specific area and engages the player's boat. (still figuring out the AI but the art is there)
- **Debug Mode**: A toggle-able debug mode displaying FPS, update stats, frame counters, hitboxes, and other debugging information.
- **Restart Game**: Press "R" to restart the game after it ends.
- **Static Map**: A static sea map with rocks at fixed positions in each playthrough.
- **Sound Effects**: Audio feedback when the boat moves.
- **Custom Assets**: Assets for the boat, sea, rocks, and enemy boat.

## Installation

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/ShiroW0lf/pirate-battleship-game.git
    ```

2. Navigate to the project directory:

    ```bash
    cd pirate-battleship-game
    ```

3. Ensure you have Java installed. If not, download the latest JDK from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

4. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse, or NetBeans).

5. Compile and run the `PirateBattleshipGame.java` file from the `src` folder to start the game.

## Controls

- **Arrow Keys**: Move the boat up, down, left, and right.
- **P Key**: Pause or resume the game.
- **R Key**: Restart the game after the boat collides or if paused.
- **Q Key**: Quit the game.
- **D Key**: Toggle debug mode to display game stats, hitboxes, and counters.
- **Mouse Click**: Start the game by clicking the "Start Game" button on the title screen.

## Game Assets

- **Boat**: A pirate ship controlled by the player (`boat.png`).
- **Sea**: Background of the game (`sea.gif`).
- **Rocks**: Various rock obstacles of different sizes (`rock1.png`, `rock2.png`, `rock3.png`).
- **Enemy Boat**: An AI-controlled enemy boat that patrols and engages (`enemyBoat.png`).

## How to Play

1. Launch the game by running `PirateBattleshipGame.java`.
2. On the title screen, click "Start Game" to begin.
3. Use the arrow keys to navigate the pirate ship across the sea, avoiding rocks and enemy patrols.
4. Press "P" to pause or resume the game.
5. Press "R" to restart the game if you lose.
6. Toggle the debug mode with "D" to see stats like FPS, counters, and hitboxes for development insights.

## Customization

- **Obstacle Placement**: Adjust the positions and sizes of the rocks by modifying the `generateRocks()` method in `GameCanvas.java`.
- **Assets**: Replace the game assets (ship, rocks, sea, enemy boat) by adding new images to the `src/assets` folder and updating their file paths in the code.
- **Sound Effects**: Customize sounds by adding audio files to the assets folder and adjusting their file paths in the game code.

## Project 2 Updates

- **Enemy Boat Art**: Added new visuals for the enemy boat.
- **Sound Effects**: Added sound when the boat moves.
- **Pause Menu**: Added a pause menu allowing players to pause, resume, reset, and quit the game.
- **Debug Mode**: Introduced a debug mode to show FPS, update stats, frame counters, hitboxes, and more.

## Future Enhancements

- **Sound Effects & Music**: Adding more sound effects and background music for an immersive experience.
- **Difficulty Levels**: Dynamic obstacles and enemy behavior for varying difficulty levels.
- **Advanced Combat**: Allow the player to engage in combat with the enemy boat and any future AI threats.

## Credits

- **Development**: [Aswin Lohani]
