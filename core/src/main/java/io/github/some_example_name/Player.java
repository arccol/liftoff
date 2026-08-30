package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Player {
    Vector2 vel;
    Vector2 accel;
    Vector2 position;
    Vector2 prevpos;
    int rad;
    Trajectory trail;
    boolean launched;

    public Player(int x, int y, int rad){
        position = new Vector2();
        accel = new Vector2();
        vel = new Vector2();
        position.set(x,y);
        prevpos = new Vector2();
        this.rad = rad;
        accel.set(0,0);
        vel.set(0,0);
    }

    public void updatePos(){
        prevpos.set(position);
        position.add(vel);
    }

    public boolean getStopped(){
        return (prevpos.sub(position).len()<0.05f);
    }

    public void applyForce(Vector2 force) {
        vel.add(force);
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(Color.WHITE);
        sr.circle(position.x, position.y,rad);
    }

    public Vector2 getPosition(){
        return position;
    }

    public void genTrail(List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce){
        trail = new Trajectory(vel, position, planets, sr, mouseForce, rad);
        trail.generate();
    }

    public Trajectory getTrail(){
        return trail;
    }

    public boolean checkColPlanet(RealPlanet target){
        Vector2 targetPos;
        targetPos = target.position;
        float difference;
        difference = position.dst(targetPos);
        return !(difference > rad + target.rad);
    }

    public void moveCol(RealPlanet target) {

        Vector2 delta = new Vector2(position).sub(target.position);
        float dist = delta.len();
        if(dist == 0) return;

        float overlap = (rad + target.rad) - dist;

        if(overlap > 0) {
            delta.nor();
            position.add(delta.cpy().scl(overlap));

            float velocityIntoSurface = vel.dot(delta);

            if(velocityIntoSurface < 0) {
                vel.sub(delta.cpy().scl(velocityIntoSurface));
            }

            vel.scl(0.9f);
            vel.add(delta.cpy().scl(0.1f));
        }
    }

    public boolean getLaunched(){
        return launched;
    }

    public void setLaunched(boolean launched){
        this.launched = launched;
    }

    public Vector2 getVel(){
        return vel;
    }

    public Vector2 getDis(){
        return prevpos.cpy().sub(position);
    }
}
