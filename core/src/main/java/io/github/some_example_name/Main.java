package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        mousePos.x = Gdx.input.getX();
        mousePos.y = 960-Gdx.input.getY();

        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }
}
