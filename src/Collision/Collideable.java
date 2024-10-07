package src.Collision;

import java.util.ArrayList;

public class Collideable implements Runnable{

    private final double rateTarget = 100.0;
    public double waitTime = 1000.0 / rateTarget;
    public double rate = 1000 / waitTime;

    private ArrayList<CollisionBody> body = new ArrayList<>();
    private ArrayList<Collideable> colliders = new ArrayList<>();

    public Collideable() {
    }

    public synchronized ArrayList<CollisionBody> getList() {
        return body;
    }
    public synchronized void addBody(CollisionBody b) {
        body.add(b);
    }

    public synchronized void dropBody(CollisionBody b) {
        body.remove(b);
    }

    public synchronized void addCollider(Collideable c) {
        colliders.add(c);
    }

    public synchronized void dropCollider(Collideable c) {
        colliders.remove(c);
    }

    public boolean collide() {
        for (Collideable c : colliders) {
            for (CollisionBody b1 : body) {
                for (CollisionBody b2: c.body) {
                    if (b1.collide(b2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            collide();
        }
    }
}