package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class RealPlanet {
    Vector2 pos;
    int rad;
    int dens;
    Vector2 tempvec;

    public RealPlanet(int x, int y, int rad, int dens){
        pos = new Vector2(x,y);
        tempvec = new Vector2(x,y);
        this.rad = rad;
        this.dens = dens;
    }

    public void draw(ShapeRenderer sr){
        sr.circle(pos.x,pos.y,rad);
    }

    public int getDens(){
        return dens;
    }

    public Vector2 getPos(){
        return pos;
    }

    public int getSize() {
        return rad;
    }
}
