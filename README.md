# PirateBattleShipGame ( game development class - Aswin)

## Overview

Welcome to **Pirate Battleship**, a 2D game where you control a pirate ship navigating the treacherous seas filled with obstacles like rocks. The objective is to guide your ship safely, avoiding collisions with rocks. If your ship hits a rock, it's game over! You can restart the game and try again by pressing the "R" key.

This game is built using Java and Swing for graphics, and features assets like pirate ships, rocks, and a sea background.

## Features

- **Title Screen**: A welcome screen where you can click "Start Game" to begin.
- **Boat Navigation**: Use the arrow keys to control the movement of the pirate ship.
- **Obstacle Collision**: The game ends if your boat hits a rock.
- **Game Over**: When the game ends, a "Game Over" screen appears with an option to restart.
- **Restart Game**: Press "R" to restart the game after it ends.
- **Static Map**: The sea map is static, and the rocks are placed at fixed positions for each playthrough.
- **Custom Assets**: The game includes custom assets for the boat, sea, and rocks.

## Installation

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/ShiroW0lf/pirate-battleship-game.git
    ```

2. Navigate to the project directory:

    ```bash
    cd pirate-battleship-game
    ```

3. Ensure you have Java installed. If not, install the latest version of the JDK from [here](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

4. Open the project in your preferred IDE (such as IntelliJ IDEA, Eclipse, or NetBeans).

5. Compile and run the `PirateBattleshipGame.java` file from the `src` folder to start the game.

## Controls

- **Arrow Keys**: Move the boat up, down, left, and right.
- **R Key**: Restart the game after the boat collides with a rock.
- **Mouse Click**: Start the game by clicking the "Start Game" button on the title screen.

## Game Assets

- **Boat**: A pirate ship controlled by the player (`boat.png`).
- **Sea**: The background of the game (`sea.gif`).
- **Rocks**: Various rock obstacles in different sizes (`rock1.png`, `rock2.png`, `rock3.png`).

## How to Play

1. Launch the game by running `PirateBattleshipGame.java`.
2. On the title screen, click "Start Game" to begin.
3. Use the arrow keys to navigate the pirate ship across the sea.
4. Avoid collisions with rocks. If you collide with a rock, the game is over.
5. Press "R" to restart the game from the beginning.

## Customization

- You can modify the positions and sizes of the rocks by adjusting the `generateRocks()` method in the `GameCanvas.java` file.
- The game's assets (ship, rocks, sea) can be replaced by adding new image files to the `src/assets` folder and adjusting their file paths in the respective code.

## Future Enhancements

- Adding sound effects and background music for a more immersive experience.
- Introducing different difficulty levels with dynamic obstacles.
- Implementing a scoring system to track how long the player can navigate without hitting rocks.
- **Tower Defense Mechanic**: Introducing a stationary tower that will attack the player's pirate ship when it comes into range.
- **Enemy AI Patrol**: Adding an AI-controlled enemy boat that will patrol a specific area. If the player's boat is detected within its patrol zone, the enemy boat will     
                       engage and attack.
- **Combat System**: Allowing the player to attack both the enemy boat and the tower. Players will need to navigate strategically, avoiding attacks while returning fire.


## Credits

- **Development**: [Aswin Lohani]


