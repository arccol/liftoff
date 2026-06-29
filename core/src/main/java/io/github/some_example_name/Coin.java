package io.github.some_example_name;
import com.badlogic.gdx.math.Vector2;

public class Coin {
    Vector2 pos;
    int rad;

    public Coin(int x, int y, int rad){
        pos = new Vector2(x,y);
        this.rad = rad;
    }

    public Vector2 getPos(){
        return pos;
    }

    public int getSize() {
        return rad;
    }
}
