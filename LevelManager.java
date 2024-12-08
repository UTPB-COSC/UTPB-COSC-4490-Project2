import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the levels in the game, providing platforms, spikes, and a goal
 * for each level. Levels are hardcoded here, but could be externalized later.
 */
public class LevelManager {
    /** The current level number. */
    public int currentLevel = 1;

    /** The screen dimensions, used to position platforms/spikes/goals. */
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Data holder for a level's platforms, spikes, and goal.
     * This is returned by the level methods (levelOne, levelTwo, etc.).
     */
    public static class LevelData {
        public final List<Platform> platforms;
        public final List<Spike> spikes;
        public final Goal goal;

        public LevelData(List<Platform> platforms, List<Spike> spikes, Goal goal) {
            this.platforms = platforms;
            this.spikes = spikes;
            this.goal = goal;
        }
    }

    /**
     * Advances to the next level. If no next level is defined,
     * it will wrap back to the first level in {@link #loadLevel()}.
     */
    public void advanceLevel() {
        currentLevel++;
    }

    /**
     * Loads the current level's data based on {@link #currentLevel}.
     * If the currentLevel does not match any defined level, it resets to level 1.
     *
     * @return the {@link LevelData} for the current level.
     */
    public LevelData loadLevel() {
        switch (currentLevel) {
            case 1: return levelOne();
            case 2: return levelTwo();
            case 3: return levelThree();
            case 4: return levelFour();
            case 5: return levelFive();
            case 6: return levelSix();
            case 7: return levelSeven();
            case 8: return levelEight();
            default:
                currentLevel = 1;
                return levelOne();
        }
    }

    /**
     * Define the first level.
     */
    private LevelData levelOne() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Platforms
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(100, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(700, screenSize.height - 400, 400, 20));

        // Spikes
        spikes.add(new Spike(300, screenSize.height - 2, 20));
        spikes.add(new Spike(500, screenSize.height - 380, 20));

        // Goal
        Goal goal = new Goal(1000, screenSize.height - 500, 30, 90);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the second level.
     */
    private LevelData levelTwo() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Platforms
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(200, screenSize.height - 200, 600, 20));
        platforms.add(new Platform(500, screenSize.height - 350, 300, 20));
        platforms.add(new Platform(800, screenSize.height - 500, 150, 20));

        // Spikes
        spikes.add(new Spike(150, screenSize.height - 2, 25));
        spikes.add(new Spike(600, screenSize.height - 340, 25));
        // Example of commented out spike if needed later
        // spikes.add(new Spike(850, screenSize.height - 490, 25));

        // Goal
        Goal goal = new Goal(910, screenSize.height - 590, 30, 90);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the third level.
     */
    private LevelData levelThree() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Platforms
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(200, screenSize.height - 200, 600, 20));
        platforms.add(new Platform(400, screenSize.height - 350, 300, 20));
        platforms.add(new Platform(650, screenSize.height - 500, 350, 20));
        platforms.add(new Platform(900, screenSize.height - 650, 200, 20));

        // Spikes
        spikes.add(new Spike(100, screenSize.height - 2, 25));
        spikes.add(new Spike(450, screenSize.height - 340, 25));
        spikes.add(new Spike(750, screenSize.height - 490, 25));

        // Goal
        Goal goal = new Goal(1000, screenSize.height - 740, 30, 90);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the fourth level.
     */
    private LevelData levelFour() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Many spikes at the bottom
        int[] spikePositions = {250, 300, 350, 400, 800, 850, 900, 950, 1000, 1050, 
                                1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500};
        for (int pos : spikePositions) {
            spikes.add(new Spike(pos, screenSize.height - 2, 20));
        }

        // Platforms going upward, requiring careful jumps
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(300, screenSize.height - 200, 40, 20));
        platforms.add(new Platform(500, screenSize.height - 300, 40, 20));
        platforms.add(new Platform(700, screenSize.height - 400, 40, 20));
        platforms.add(new Platform(900, screenSize.height - 500, 40, 20));
        platforms.add(new Platform(1100, screenSize.height - 600, 40, 20));
        platforms.add(new Platform(1350, screenSize.height - 700, 40, 20));

        // Goal placed near the top
        Goal goal = new Goal(1350, screenSize.height - 200, 110, 30);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the fifth level.
     */
    private LevelData levelFive() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Spikes scattered at the bottom
        int[] scatteredSpikes = {100, 300, 600, 650, 1200, 1450, 1500, 1800};
        for (int pos : scatteredSpikes) {
            spikes.add(new Spike(pos, screenSize.height - 2, 20));
        }

        // Platforms with varied lengths and positions
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(150, screenSize.height - 200, 150, 20));
        platforms.add(new Platform(500, screenSize.height - 350, 100, 20));
        platforms.add(new Platform(850, screenSize.height - 200, 70, 20));
        platforms.add(new Platform(1000, screenSize.height - 500, 50, 20));
        platforms.add(new Platform(1150, screenSize.height - 650, 100, 20));
        platforms.add(new Platform(1300, screenSize.height - 300, 80, 20));
        platforms.add(new Platform(1600, screenSize.height - 400, 120, 20));
        platforms.add(new Platform(1750, screenSize.height - 200, 40, 20));
        platforms.add(new Platform(700, screenSize.height - 150, 100, 20));
        // Additional spikes near platforms
        spikes.add(new Spike(750, screenSize.height - 200, 20));
        spikes.add(new Spike(850, screenSize.height - 200, 20));

        // Staggered upward platforms near the end
        platforms.add(new Platform(1600, screenSize.height - 650, 80, 20));
        platforms.add(new Platform(1700, screenSize.height - 750, 80, 20));
        platforms.add(new Platform(1800, screenSize.height - 850, 80, 20));

        // Goal
        Goal goal = new Goal(1400, screenSize.height - 500, 110, 20);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the sixth level.
     */
    private LevelData levelSix() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Spike maze at the bottom
        for (int i = 200; i < screenSize.width - 200; i += 100) {
            spikes.add(new Spike(i, screenSize.height - 2, 20));
        }

        // Platforms and spikes arranged in sections
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(100, screenSize.height - 200, 200, 20));
        platforms.add(new Platform(400, screenSize.height - 400, 80, 20));
        platforms.add(new Platform(600, screenSize.height - 400, 80, 20));
        spikes.add(new Spike(680, screenSize.height - 390, 20));
        platforms.add(new Platform(400, screenSize.height - 100, 300, 20));
        platforms.add(new Platform(800, screenSize.height - 550, 100, 20));
        platforms.add(new Platform(950, screenSize.height - 650, 60, 20));
        platforms.add(new Platform(1100, screenSize.height - 250, 60, 20));
        platforms.add(new Platform(1250, screenSize.height - 350, 120, 20));
        spikes.add(new Spike(1300, screenSize.height - 520, 20));
        platforms.add(new Platform(1450, screenSize.height - 400, 120, 20));
        spikes.add(new Spike(1500, screenSize.height - 620, 20));

        platforms.add(new Platform(1500, screenSize.height - 350, 60, 20));
        platforms.add(new Platform(1450, screenSize.height - 550, 60, 20));
        platforms.add(new Platform(1350, screenSize.height - 250, 60, 20));

        // Additional spikes at bottom
        spikes.add(new Spike(1300, screenSize.height - 2, 20));
        spikes.add(new Spike(1450, screenSize.height - 2, 20));
        spikes.add(new Spike(1400, screenSize.height - 2, 20));
        spikes.add(new Spike(1500, screenSize.height - 2, 20));

        // Goal
        Goal goal = new Goal(1400, screenSize.height - 270, 120, 30);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the seventh level.
     */
    private LevelData levelSeven() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // Multiple platforms at same height
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(100, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(300, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(500, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(700, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(900, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(1100, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(1300, screenSize.height - 200, 500, 20));
        platforms.add(new Platform(1500, screenSize.height - 200, 500, 20));

        // Goal placed high
        Goal goal = new Goal(1500, screenSize.height - 900, 30, 110);

        return new LevelData(platforms, spikes, goal);
    }

    /**
     * Define the eighth level.
     */
    private LevelData levelEight() {
        List<Platform> platforms = new ArrayList<>();
        List<Spike> spikes = new ArrayList<>();

        // A single tall platform and a goal above
        platforms.add(new Platform(1, screenSize.height - 10, 2600, 20));
        platforms.add(new Platform(200, screenSize.height - 510, 20, 500));
        Goal goal = new Goal(40, screenSize.height - 30, 110, 30);

        return new LevelData(platforms, spikes, goal);
    }
}
