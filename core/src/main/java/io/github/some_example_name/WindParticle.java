package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindParticle {
    Vector2 pos;
    Random rand;
    int rad;
    List<Vector2> trail;
    Vector2 randomvec;

    public WindParticle(int x, int y, int rad){
        pos = new Vector2();
        rand = new Random();
        pos.set(x,y);
        this.rad = rad;
        trail = new ArrayList<>();
        randomvec = new Vector2();
        randomvec.set(rand.nextFloat(-0.001f,0.001f), rand.nextFloat(-0.001f,0.001f));
    }

    public void draw(ShapeRenderer sr){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setColor(0f,0f,0f,0.02f);
        sr.circle(pos.x,pos.y,rad);

        for (int i = 0; i < trail.size(); i++) {
            if (trail.size() - 20 < i) {
                sr.setColor(0f, 0f, 0f, 0.02f-(trail.size()-i)/1000f);
                sr.circle(trail.get(i).x, trail.get(i).y, 5);
            }
        }

    }

    public void updatePos(Vector2 wind){
        pos.add(wind.cpy().scl(50));
        pos.add(randomvec.cpy().scl(1000));
        if(pos.x<-rad){
            pos.set(Gdx.graphics.getWidth()+rad,pos.y);
        }
        if(pos.x>Gdx.graphics.getWidth()+rad){
            pos.set(-rad,pos.y);
        }
        if(pos.y<-rad){
            pos.set(pos.x,Gdx.graphics.getHeight()+rad);
        }
        if(pos.y>Gdx.graphics.getHeight()+rad){
           pos.set(pos.x,-rad);
        }
        trail.add(pos.cpy());
    }
}
