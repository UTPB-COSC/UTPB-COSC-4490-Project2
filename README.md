# BombMakeGoBoom

**BombMakeGoBoom** is a Java-based game featuring a player, enemies, bombs, and a destructible map. The player can place bombs on a grid, which explode after a short delay, potentially destroying blocks and eliminating enemies within the blast radius. This project includes key elements such as animated graphics, sound effects, game state management, and real-time game mechanics.

## Features

- **Player & Enemy Movement**: The player can move within the grid. An enemy is also present, and it reacts to bomb explosions.
- **Bomb Placement & Explosion**: Players can place one bomb at a time, which explodes after a set delay, affecting nearby blocks and the enemy.
- **Game State Management**: The game has different states such as Running, Paused, Game Over, and Game Won.
- **Graphics & Animations**: Custom images and animations for bombs and explosions. ***Fingers Crossed*** the picture for the bomb explosion works
- **Sound Effects**: Explosion sound when the bomb detonates.
- **Debug Mode**: Displays additional information, such as FPS, player and enemy positions, and the bomb’s blast radius.

## Project Structure

### `GameCanvas` Class
### `Game` Class
### `Player` Class
### `Enemy` Class
### `Bomb` Class
### `Tile` Class

### Game Controls:
   - Use arrow keys to navigate the player.
   - Press the space bar to place a bomb.
   - Use the `P` or `Escape` key to pause/unpause the game.
   - Press `K` in debug mode to view the blast radius around the current bomb position.

### Debug Mode:
   - Debug mode displays additional information, including FPS, player and enemy positions, and the bomb’s blast radius.

### Bomb Explosion Logic

The `Bomb` class handles all bomb-related functionality, including:
1. **Image and Sound Loading**: Loads bomb and explosion images, as well as the explosion sound.
2. **Explosion Timer**: Starts a timer upon placement and triggers the explosion after a predefined delay.
3. **Explosion Radius**: When the bomb explodes, it affects tiles in four directions (up, down, left, right). It can clear blocks and eliminate enemies within this radius.
4. **Game Effects**: If the player is within the explosion radius, the game ends (`gameOver`). If an enemy is within the radius, they are destroyed, and if no enemies remain, the game state changes to `gameWon`.

