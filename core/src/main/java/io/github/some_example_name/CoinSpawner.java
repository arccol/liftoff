package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class CoinSpawner {
    List<Coin> coinList;
    int totalcoins;

    public CoinSpawner(List<Coin> coinList){
        this.coinList = coinList;
        totalcoins = 0;
    }

    public void draw(ShapeRenderer sr, Batch batch){
        sr.setColor(Color.GOLD);
        for(Coin coin : coinList){
            sr.circle(coin.getPos().x, coin.getPos().y, coin.getSize());
        }
        sr.setColor(Color.WHITE);
    }

    public void pickupCoins(Vector2 playerPos){
        for(Coin coin : coinList){
            if(coin.getPos().dst(playerPos)<coin.getSize()+15){
                coinList.remove(coin);
                totalcoins++;
                break;
            }
        }
    }

    public int getCoins(){
        return totalcoins;
    }

    public void updateCoins(int amount){
        totalcoins+=amount;
    }
}
