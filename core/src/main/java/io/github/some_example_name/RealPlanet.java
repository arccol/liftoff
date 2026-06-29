package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public class RealPlanet {
    Vector2 position;
    int rad;
    int dens;
    Vector2 tempvec;
    boolean placed;
    CoinSpawner coinSpawner;
    boolean overlap;
    Vector2 randomVec;
    Random rand;
    int offset = 30;
    List<Coin> coinList;

    public RealPlanet(int x, int y, int rad, int dens, List<Coin> coinList){
        position = new Vector2(x,y);
        tempvec = new Vector2(x,y);
        this.rad = rad;
        this.dens = dens;
        overlap = false;
        randomVec = new Vector2();
        rand = new Random();
        randomVec.set(0,0);
        this.coinList = coinList;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(new Color(1-((float) dens /5),1-((float) dens /5),1-((float) dens /5),1)); // darker = denser
        sr.circle(position.x, position.y,rad);
        sr.setColor(Color.WHITE);
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

    public void attachToMouse(Vector2 mousePos){
        position = mousePos;
    }

    public void generateCoins(){
        for (int i = 0; i < 3; i++) {

            Vector2 coinPos = new Vector2();

            float angle = rand.nextFloat() * 360;
            coinPos.set(
                (float)Math.cos(Math.toRadians(angle)),
                (float)Math.sin(Math.toRadians(angle))
            );

            coinPos.setLength(rad + offset);
            coinPos.add(position);

            boolean overlap = false;

            for (Coin coin : coinList) {
                if (coin.getPos().dst(coinPos) < coin.getSize() + 10) {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                coinList.add(new Coin((int)coinPos.x, (int)coinPos.y, 5));
            }
        }
    }
}
