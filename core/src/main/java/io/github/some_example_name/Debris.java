package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Debris {
    public static float gravityScale = 0.2f;

    Vector2 position;
    Vector2 prevpos;
    Vector2 vel;
    int size;
    boolean hit;
    TextureRegion debris;
    Random rand;
    int rot;
    int type;
    boolean hidden;

    public Debris(int x, int y, int size, Texture debrisTexture1, Texture debrisTexture2, Texture debrisTexture3){
        position = new Vector2(x, y);
        prevpos = new Vector2(x, y);
        vel = new Vector2();
        this.size = size;
        hit = false;
        rand = new Random();
        debris = new TextureRegion();
        rot = rand.nextInt(1,360);
        type = rand.nextInt(0,3)+1;
        switch(type){
            case 1:
                debris.setRegion(debrisTexture1);
                break;
            case 2:
                debris.setRegion(debrisTexture2);
                break;
            case 3:
                debris.setRegion(debrisTexture3);
                break;
        }
    }

    public void updatePos(){
        prevpos.set(position);
        position.add(vel);
        vel.scl(0.95f);
    }

    public void applyForce(Vector2 force){
        vel.add(force);
    }

    public void draw(SpriteBatch batch){
        batch.begin();
        batch.setColor(Color.WHITE);
        if(!hidden) {
            switch (type) {
                case 1:
                    batch.draw(debris, position.x - size, position.y - size, 12f, 11f, 24f, 21f, 1f, 1f, rot);
                    break;
                case 2:
                    batch.draw(debris, position.x - size, position.y - size, 12f, 12f, 24f, 24f, 1f, 1f, rot);
                    break;
                case 3:
                    batch.draw(debris, position.x - size, position.y - size, 12f, 11f, 24f, 22f, 1f, 1f, rot);
                    break;
            }
        }
        batch.end();
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
        if(hidden){
            return false;
        }
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

    public void delete(){
        hidden = true;
    }
}
