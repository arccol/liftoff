package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.javadoc.internal.doclets.formats.html.Table;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    List<RealPlanet> planets;
    List<Button> buttons;
    Player player;
    NavigationHandler screenHandler;
    ShapeRenderer sr;
    Vector2 mousePos;
    private Stage stage;
    private Table table;

    // https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui#stage-setup

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
            case "levelScreen":
                buttons = screenHandler.getButtons("levelScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
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
                            screenHandler.setScreen(button.getTitle());
                        }
                    }
                }
                sr.end();
                break;
            case "settingsScreen":
                buttons = screenHandler.getButtons("settingsScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
                            screenHandler.setScreen(button.getTitle());
                        }
                    }
                }
                sr.end();
                break;
            case "customiseScreen":
                buttons = screenHandler.getButtons("customiseScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
                            screenHandler.setScreen(button.getTitle());
                        }
                    }
                }
                sr.end();
                break;
            case "inGameScreen":
                buttons = screenHandler.getButtons("inGameScreen");
                sr.begin(ShapeRenderer.ShapeType.Filled);
                for(Button button : buttons){
                    button.draw(sr);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        if (button.getClicked(mousePos)) {
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
