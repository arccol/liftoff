package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Player {
    Vector2 vel;
    Vector2 accel;
    Vector2 position;
    int rad;
    Trajectory trail;
    boolean launched;

    public Player(int x, int y, int rad){
        position = new Vector2();
        accel = new Vector2();
        vel = new Vector2();
        position.set(x,y);
        this.rad = rad;
        accel.set(0,0);
        vel.set(0,0);
    }

    public void updatePos(){
        position.add(vel);
    }

    public void applyForce(Vector2 force) {
        vel.add(force);
    }

    public void draw(ShapeRenderer sr){
        sr.circle(position.x, position.y,rad);
    }

    public Vector2 getPosition(){
        return position;
    }

    public void genTrail(List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce){
        trail = new Trajectory(vel, position, planets, sr, mouseForce);
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
        if(difference>rad+target.rad){
            return false;
        }else{
            return true;
        }
    }

    public void moveCol(RealPlanet target) {

        Vector2 delta = new Vector2(position).sub(target.position);
        float dist = delta.len();

        float overlap = (rad + target.rad) - dist;

        if (overlap > 0) {
            delta.nor();

            position.add(delta.cpy().scl(overlap*rad/50));

            float vector1 = vel.dot(delta);

            vel.sub(delta.cpy().scl(vector1));

            vel.scl(0.9f);
        }
    }

    public boolean getLaunched(){
        return launched;
    }

    public void setLaunched(boolean launched){
        this.launched = launched;
    }
}
