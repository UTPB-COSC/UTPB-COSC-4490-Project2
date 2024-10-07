package src.Threads;

import src.Settings.Settings;

public abstract class RateLimited implements Runnable{

    private final double rateTarget;
    private final double waitTime;
    private double rate;

    public RateLimited() {
        rateTarget = Settings.UPDATERATE;
        waitTime = 1000.0 / rateTarget;
        rate = 1000 / waitTime;
    }

    public RateLimited(double target) {
        rateTarget = target;
        waitTime = 1000.0/rateTarget;
        rate = 1000.0/waitTime;
    }

    public double getRate() {
        return rate;
    }

    public long calcDelay(long startTime) {
        long sleep = (long) waitTime - (System.nanoTime() - startTime) / 1000000;
        rate = 1000.0 / Math.max(waitTime - sleep, waitTime);
        return sleep;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException intEx) {

        }
    }

    public void limit(long startTime) {
        sleep(calcDelay(startTime));
    }
}
