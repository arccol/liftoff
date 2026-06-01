package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Box {
    float x, y, w, h;

    public Box(float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void draw(ShapeRenderer sr){
        sr.rect(x,y,w,h);
    }

}
