package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    List<RealPlanet> planets;
    List<Button> buttons;
    Player player;
    NavigationHandler screenHandler;
    ShapeRenderer sr;

    @Override
    public void create() {
        sr = new ShapeRenderer();
        buttons = new ArrayList<>();
        planets = new ArrayList<>();
        player = new Player(0,0,10);
        screenHandler = new NavigationHandler();
        screenHandler.setScreen("gameScreen");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        switch(screenHandler.getCurrentScreen()){
            case "gameScreen":
                sr.begin(ShapeRenderer.ShapeType.Filled);
                player.draw(sr);
                sr.end();
                break;
            case "homeScreen":
                screenHandler.getButtons("homeScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                }
        }
    }

    @Override
    public void dispose() {

    }
}
