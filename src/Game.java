package src;

import src.Threads.Engine;

public class Game {
    public static void main(String[] args)
    {
        Engine game = new Engine();

        Thread logicLoop = new Thread(game);
        logicLoop.start();
    }
}
