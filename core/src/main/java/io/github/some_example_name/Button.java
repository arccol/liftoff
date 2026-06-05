package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Button {
    String title;
    int x;
    int y;
    int rad;

    public Button(String title, int x, int y, int rad){
        this.title = title;
        this.x = x;
        this.y = y;
        this.rad = rad;
    }

    public boolean getClicked(Vector2 mousePos){
        float dx = mousePos.x - x;
        float dy = mousePos.y - y;
        return (dx * dx + dy * dy) <= (rad * rad);
    }

    public void draw(ShapeRenderer sr){
        sr.circle(x,y,rad);
    }

    public String getTitle(){
        return title;
    }
}
