package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Debris {
    public static final float GRAVITY_SCALE = 0.2f;

    Vector2 position;
    Vector2 prevpos;
    Vector2 vel;
    int size;
    boolean hit;

    public Debris(int x, int y, int size){
        position = new Vector2(x, y);
        prevpos = new Vector2(x, y);
        vel = new Vector2();
        this.size = size;
        hit = false;
    }

    public void updatePos(){
        prevpos.set(position);
        position.add(vel);
        vel.scl(0.95f);
    }

    public void applyForce(Vector2 force){
        vel.add(force);
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(Color.GRAY);
        sr.rect(position.x - size, position.y - size, size * 2, size * 2);
        sr.setColor(Color.WHITE);
    }

    public Vector2 getPosition(){
        return position;
    }

    public int getSize(){
        return size;
    }

    public boolean isHit(){
        return hit;
    }


    private float boundingRadius(){
        return size * 1.4142f;
    }

    public boolean checkColPlayer(Player player){
        float difference = position.dst(player.getPosition());
        return !(difference > boundingRadius() + player.rad);
    }

    public void resolveColPlayer(Player player){
        Vector2 delta = new Vector2(position).sub(player.position);
        float dist = delta.len();
        if(dist == 0){
            delta.set(1, 0);
            dist = 0.01f;
        } else {
            delta.scl(1f / dist);
        }

        float overlap = (boundingRadius() + player.rad) - dist;
        if(overlap <= 0) return;


        position.add(new Vector2(delta).scl(overlap * 0.95f));
        player.position.sub(new Vector2(delta).scl(overlap * 0.05f));

        Vector2 relativeVel = new Vector2(player.vel).sub(vel);
        float velAlongNormal = relativeVel.dot(delta);

        if(velAlongNormal > 0){
            float kick = velAlongNormal * 2.5f;
            vel.add(new Vector2(delta).scl(kick));
            player.vel.sub(new Vector2(delta).scl(velAlongNormal * 0.05f));
        }

        hit = true;
    }

    public boolean checkColPlanet(RealPlanet target){
        float difference = position.dst(target.position);
        return !(difference > boundingRadius() + target.rad);
    }

    public void moveCol(RealPlanet target){
        Vector2 delta = new Vector2(position).sub(target.position);
        float dist = delta.len();
        if(dist == 0) return;

        float overlap = (boundingRadius() + target.rad) - dist;

        if(overlap > 0){
            delta.nor();
            position.add(delta.cpy().scl(overlap));

            float velocityIntoSurface = vel.dot(delta);
            if(velocityIntoSurface < 0){
                vel.sub(delta.cpy().scl(velocityIntoSurface * 1.6f));
            }
        }
    }
}
