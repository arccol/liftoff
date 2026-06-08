package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Player {
    Vector2 vel;
    Vector2 accel;
    Vector2 pos;
    int rad;
    Trajectory trail;

    public Player(int x, int y, int rad){
        pos = new Vector2();
        accel = new Vector2();
        vel = new Vector2();
        pos.set(x,y);
        this.rad = rad;
        accel.set(0,0);
        vel.set(0,0);
    }

    public void updatePos(){
        pos.add(vel);
    }

    public void applyForce(Vector2 force) {
        vel.add(force);
    }

    public void draw(ShapeRenderer sr){
        sr.circle(pos.x,pos.y,rad);
    }

    public Vector2 getPos(){
        return pos;
    }

    public void genTrail(List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce){
        trail = new Trajectory(vel, pos, planets, sr, mouseForce);
        trail.generate();
    }

    public Trajectory getTrail(){
        return trail;
    }
}
