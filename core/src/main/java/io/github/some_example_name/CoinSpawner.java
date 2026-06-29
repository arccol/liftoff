package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class CoinSpawner {
    List<Coin> coinList;

    public CoinSpawner(List<Coin> coinList){
        this.coinList = coinList;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(Color.GOLD);
        for(Coin coin : coinList){
            sr.circle(coin.getPos().x, coin.getPos().y, coin.getSize());
        }
        sr.setColor(Color.WHITE);
    }

    public void pickupCoins(Vector2 playerPos){
        coinList.removeIf(coin -> coin.getPos().dst(playerPos) < coin.getSize() + 15
        );

    }
}
