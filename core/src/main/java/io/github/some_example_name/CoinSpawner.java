package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class CoinSpawner {
    Vector2 pos;
    int rad;

    public CoinSpawner(int x, int y, int rad) {
        pos = new Vector2(x, y);
        this.rad = rad;
    }

    public void draw(ShapeRenderer sr) {
        sr.setColor(Color.GOLD);
        sr.circle(pos.x, pos.y, rad);
        sr.setColor(Color.WHITE);

        // create coin
    }

    public boolean overlap(Vector2 target, int targetrad){
        Vector2 targetPos;
        targetPos = target;
        float difference;
        difference = pos.dst(targetPos);
        return !(difference > rad + targetrad);
    }
}
