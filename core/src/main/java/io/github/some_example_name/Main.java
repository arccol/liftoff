package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private boolean mDown;
    private boolean mWasDown;
    private int bufferFrames;
    private boolean ready;
    private Vector2 staticMousePos;
    Random rand;

    @Override
    public void create() {
        rand = new Random();
        sr = new ShapeRenderer();
        buttons = new ArrayList<>();
        planets = new ArrayList<>();
        player = new Player(600,400,10);
        player.setLaunched(false);
        mousePos = new Vector2();
        staticMousePos = new Vector2();
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
        bufferFrames = 0;

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
        bufferFrames = 0;

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
        bufferFrames = 0;

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
        bufferFrames = 0;

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

        if(ready) {
            mDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        }
        if(!ready) {
            ready = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.X)){
            ready = false;
        }

        mousePos.set(
            Gdx.input.getX(),
            Gdx.graphics.getHeight() - Gdx.input.getY()
        );

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            staticMousePos.set(mousePos.cpy());
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);

        switch(currentScreen){
            case GAME:

                if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
                    int rd = rand.nextInt(5);
                    int rr = rand.nextInt(20)+5;
                    planets.add(new RealPlanet((int) mousePos.x, (int) mousePos.y, rr, rd));
                }
                for(RealPlanet planet : planets) {
                    if (planet == null) {
                        continue;
                    }
                    planet.draw(sr);
                    if (player.getLaunched()) {
                        if(player.checkColPlanet(planet)) {
                            player.moveCol(planet);
                        }
                        Vector2 planetForce = planet.getPosition().cpy()
                            .sub(player.getPosition())
                            .scl(planet.getDens() * planet.getSize() / 20000f);

                        player.applyForce(planetForce.scl(1/planetForce.len()));
                    }
                }

                player.updatePos();

                Vector2 mouseForce = mousePos.cpy()
                    .sub(staticMousePos)
                    .scl(0.03f)
                    .scl(-1);

                mouseForce.scl(mouseForce.len());
                mouseForce.limit(20f);

                if(mDown&&!player.getLaunched()&&ready){
                    player.genTrail(planets,sr,mouseForce);
                    sr.setColor(Color.DARK_GRAY);
                    sr.circle(staticMousePos.x,staticMousePos.y,10);
                    Vector2 ghostMouse = mousePos.cpy()
                            .sub(staticMousePos)
                            .limit(150)
                            .add(staticMousePos);
                    sr.rectLine(ghostMouse,staticMousePos,6);
                    sr.circle(ghostMouse.x,ghostMouse.y,5);
                    sr.setColor(Color.WHITE);
                }

                if(!player.getLaunched()&&!mDown&&mWasDown&&bufferFrames>10){
                    player.setLaunched(true);
                    player.applyForce(mouseForce);
                }

                player.draw(sr);
        }
        sr.end();
        mWasDown = mDown;
        bufferFrames++;
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
