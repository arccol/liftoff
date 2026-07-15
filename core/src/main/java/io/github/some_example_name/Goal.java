package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public class Goal {
    Vector2 position;
    int rad;
    int dens;
    boolean overlap;
    Vector2 randomVec;
    Random rand;

    public Goal(int x, int y, int rad, int dens){
        position = new Vector2(x,y);
        this.rad = rad;
        this.dens = dens;
        overlap = false;
        randomVec = new Vector2();
        rand = new Random();
        randomVec.set(0,0);
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(new Color(1-((float) dens /5),1-((float) dens /5),1-((float) dens /5),1)); // darker = denser
        sr.circle(position.x, position.y,rad);
        sr.setColor(Color.WHITE);
        sr.rectLine(position,position.cpy().add(randomVec),10);
    }

    public int getDens(){
        return dens;
    }

    public Vector2 getPosition(){
        return position;
    }

    public int getSize() {
        return rad;
    }
}
