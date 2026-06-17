package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Button extends Actor {
    TextureRegion region;

    public Button(){
        region = new TextureRegion(texture,100,100);
        setBounds(region.getRegionX(),region.getRegionY(),region.getRegionWidth(),region.getRegionHeight());
    }

    public void draw(Batch batch, float parentAlpha){
        Color color = getColor();
        batch.setColor(color.r,color.g,color.b,color.a*parentAlpha);
        batch.draw(region,getX(),getY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
    }
}
