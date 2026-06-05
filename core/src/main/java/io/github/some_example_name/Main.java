package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Main extends ApplicationAdapter {
    List<RealPlanet> planets;
    List<Button> buttons;
    Player player;
    NavigationHandler screenHandler;
    ShapeRenderer sr;
    Vector2 mousePos;

    @Override
    public void create() {
        mousePos = new Vector2();
        sr = new ShapeRenderer();
        buttons = new ArrayList<>();
        planets = new ArrayList<>();
        player = new Player(0,0,10);
        screenHandler = new NavigationHandler();
        screenHandler.setScreen("homeScreen");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        mousePos.x = Gdx.input.getX();
        mousePos.y = 960-Gdx.input.getY();
        switch(screenHandler.getCurrentScreen()){
            case "gameScreen":
                buttons = screenHandler.getButtons("gameScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                //player.draw(sr);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
                            System.out.println(button.getTitle());
                            screenHandler.setScreen(button.getTitle());
                        }
                    }
                }
                sr.end();
                break;
            case "homeScreen":
                buttons = screenHandler.getButtons("homeScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
                            System.out.println(button.getTitle());
                            screenHandler.setScreen(button.getTitle());
                        }
                    }
                }
                sr.end();
                break;
        }
    }

    @Override
    public void dispose() {

    }
}
