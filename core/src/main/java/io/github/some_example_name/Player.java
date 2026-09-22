package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player {
    Vector2 vel;
    Vector2 accel;
    Vector2 position;
    Vector2 prevpos;
    int rad;
    Trajectory traj;
    boolean launched;
    boolean launchAvailable;
    float distanceToPlanet;
    Texture playerTexture;
    TextureRegion player;
    Vector2 movementVector;
    List<Vector2> trail;
    boolean win;

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
        win = false;
        trail = new ArrayList<>();
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

        trail.add(prevpos.cpy());
    }

    public void applyForce(Vector2 force) {
        vel.add(force);
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(player,position.x-200,position.y-400, 200, 400, 400, 800, 0.05f, 0.05f, (float) getAngle(movementVector)-90f);
        batch.end();
    }

    public void drawTrail(ShapeRenderer sr){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(1f, 0.5f, 0f, 0.1f);

        for (int i = 0; i < trail.size(); i++) {
            if (trail.size() - 200 < i) {
                sr.setColor(0.5f, 0.5f, 1f, 1f-(trail.size()-i)/100f);
                sr.circle(trail.get(i).x, trail.get(i).y, 2);
            }
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public Vector2 getPosition(){
        return position;
    }

    public void genTrail(List<RealPlanet> planets, ShapeRenderer sr, Vector2 mouseForce, Vector2 wind){
        traj = new Trajectory(vel, position, planets, sr, mouseForce, rad, wind);
        traj.generate();
    }

    public Trajectory getTraj(){
        return traj;
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

        if(target.finish){
            win = true;
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

    public boolean getWin(){
        return win;
    }

    public void setWin(boolean win){
        this.win = win;
    }

    public void setDisToPlanet(float d){
        distanceToPlanet = d;
    }

    public float getDisToPlanet(){
        return distanceToPlanet;
    }
}
