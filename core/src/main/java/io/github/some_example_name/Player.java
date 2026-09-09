package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import javax.swing.*;
import java.util.List;

public class Player {
    Vector2 vel;
    Vector2 accel;
    Vector2 position;
    Vector2 prevpos;
    int rad;
    Trajectory trail;
    boolean launched;
    boolean launchAvailable;
    float distanceToPlanet;
    Texture playerTexture;
    TextureRegion player;
    Vector2 movementVector;

    public Player(int x, int y, int rad, Texture playerTexture){
        position = new Vector2();
        accel = new Vector2();
        vel = new Vector2();
        position.set(x,y);
        prevpos = new Vector2();
        movementVector = new Vector2();
        movementVector.set(0,0);
        this.rad = rad;
        this.playerTexture = playerTexture;
        player = new TextureRegion();
        player.setRegion(playerTexture);
        accel.set(0,0);
        vel.set(0,0);
        distanceToPlanet = 9999;
    }

    public double getAngle(Vector2 vector){
        double rads = Math.atan2(vector.y, vector.x);
        double degs = Math.toDegrees(rads);
        return degs;
    }

    public void updatePos(){
        position.add(vel);
        movementVector.set(position.cpy().sub(prevpos));
        prevpos.set(position.cpy());
    }

    public void applyForce(Vector2 force) {
        vel.add(force);
    }

    public void draw(ShapeRenderer sr, SpriteBatch batch) {
        sr.setColor(Color.WHITE);
        //sr.circle(position.x, position.y,rad);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(player,position.x-10,position.y-13, 11, 14, 22, 28, 1f, 1f, (float) getAngle(movementVector)-90f);
        batch.end();
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

    public boolean getLaunchAvailable() {
        return (prevpos.cpy().sub(position).len()<1f&&distanceToPlanet<50);
    }

    public void setLaunchAvailable(boolean b) {
        launchAvailable = b;
    }

    public void setDisToPlanet(float d){
        distanceToPlanet = d;
    }

    public float getDisToPlanet(){
        return distanceToPlanet;
    }
}
