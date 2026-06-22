package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jdk.javadoc.internal.doclets.formats.html.Table;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    List<RealPlanet> planets;
    List<Button> buttons;
    Player player;
    ShapeRenderer sr;
    Vector2 mousePos;
    private Stage stage;
    private Skin skin;
    enum Screen{
        HOME,
        SETTINGS,
        GAME,
        CUSTOMISE
    }
    private Screen currentScreen;

    @Override
    public void create() {
        sr = new ShapeRenderer();
        buttons = new ArrayList<>();
        planets = new ArrayList<>();
        player = new Player(0,0,10);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        changeScreen(Screen.HOME);
        Gdx.graphics.setResizable(false);
    }

    public void changeScreen(Screen screen){
        currentScreen = screen;
        stage.clear();
        switch(screen){
            case HOME:
                createHomeScreen();
                break;
            case SETTINGS:
                createSettingsScreen();
                break;
            case GAME:
                createGameScreen();
                break;
            case CUSTOMISE:
                createCustomiseScreen();
                break;
        }
    }

    private void createHomeScreen(){
        TextButton play = new TextButton("Play", skin);
        play.setPosition(565,350);
        play.setSize(150,80);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.GAME);
            }
        });

        TextButton settings = new TextButton("Settings", skin);
        settings.setPosition(290, 350);
        settings.setSize(150,80);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.SETTINGS);
            }
        });

        TextButton customise = new TextButton("Customise", skin);
        customise.setPosition(840, 350);
        customise.setSize(150,80);
        customise.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.CUSTOMISE);
            }
        });

        stage.addActor(play);
        stage.addActor(settings);
        stage.addActor(customise);
    }

    private void createSettingsScreen(){
        TextButton back = new TextButton("Back", skin);
        back.setPosition(0, 900);
        back.setSize(100, 60);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.HOME);
            }
        });
        stage.addActor(back);
    }

    private void createCustomiseScreen(){
        TextButton back = new TextButton("Back", skin);
        back.setPosition(0, 900);
        back.setSize(100, 60);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.HOME);
            }
        });

        stage.addActor(back);
    }

    private void createGameScreen() {
        TextButton back = new TextButton("Back", skin);
        back.setPosition(0, 900);
        back.setSize(100, 60);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.HOME);
            }
        });

        stage.addActor(back);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        switch(currentScreen){
            case GAME:
                // game loop
        }
        sr.end();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }
}
