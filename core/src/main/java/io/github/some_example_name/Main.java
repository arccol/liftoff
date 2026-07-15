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
    List<Coin> coinList;
    CoinSpawner coinSpawner;
    Player player;
    ShapeRenderer sr;
    Vector2 mousePos;
    private Goal goal;
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
        coinList = new ArrayList<>();
        coinSpawner = new CoinSpawner(coinList);
        rand = new Random();
        sr = new ShapeRenderer();
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
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            ready = false;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            bufferFrames=0;
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
                gameLoop();
                break;
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

    public void gameLoop(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
            int rd = rand.nextInt(4)+1;
            int rr = rand.nextInt(80)+5;
            RealPlanet p = new RealPlanet((int) mousePos.x, (int) mousePos.y, rr, rd, coinList);
            p.generateCoins();
            planets.add(p);

        }
        for(RealPlanet planet : planets) {
            if (planet == null) {
                continue;
            }
            planet.draw(sr);
            if (player.getLaunched()) {
                if(player.checkColPlanet(planet)) {
                    player.moveCol(planet);
                    if(player.getDis().len()<1f&&!player.getAbletolaunch()&&bufferFrames>5){
                        bufferFrames=0;
                        player.setAbletolaunch(true);
                    }
                }

                Vector2 planetDir = planet.getPosition().cpy()
                    .sub(player.getPosition());

                float distance = planetDir.len();
                float gravityRadius = planet.getSize() * 100f;

                if(distance < gravityRadius && distance > 1) {
                    planetDir.nor();

                    float strength = (planet.getDens() * planet.getSize() * 20)
                        / (distance * distance);

                    strength = Math.min(strength, 5f);

                    player.applyForce(
                        planetDir.scl(strength)
                    );
                }
            }
        }

        player.updatePos();
        coinSpawner.pickupCoins(player.getPosition());

        Vector2 mouseForce = mousePos.cpy()
            .sub(staticMousePos)
            .scl(0.03f)
            .scl(-1);

        mouseForce.scl(mouseForce.len());
        mouseForce.limit(10f);

        if(mDown&&(!player.getLaunched()||player.getAbletolaunch())&&ready){
            player.genTrail(planets,sr,mouseForce);
            Vector2 ghostMouse = mousePos.cpy()
                .sub(staticMousePos)
                .limit(100)
                .add(staticMousePos);
            int length = (int) ghostMouse.cpy().sub(staticMousePos).len();
            sr.setColor(new Color((float)length/100,0.5f-(float)length/300,0.1f, 1));
            sr.rectLine(ghostMouse,staticMousePos,7-((float)length/30));
            sr.setColor(Color.DARK_GRAY);
            sr.circle(staticMousePos.x,staticMousePos.y,10);
            sr.circle(ghostMouse.x,ghostMouse.y,5);
            Vector2 direction = ghostMouse.cpy()
                .sub(staticMousePos)
                .nor()
                .scl(-1);
            int arrowLength = 20;
            Vector2 tip = staticMousePos.cpy()
                .add(direction.cpy().scl(arrowLength));
            Vector2 side = new Vector2(-direction.y,direction.x);
            Vector2 left = staticMousePos.cpy()
                .add(side.cpy().scl(10));
            Vector2 right = staticMousePos.cpy()
                .sub(side.cpy().scl(10));
            sr.triangle(tip.x,tip.y,left.x,left.y,right.x,right.y);
            sr.setColor(Color.WHITE);
        }


        if((!player.getLaunched()||player.getAbletolaunch())&&!mDown&&mWasDown&&bufferFrames>30){
            bufferFrames=0;
            player.setLaunched(true);
            player.setAbletolaunch(false);
            player.applyForce(mouseForce);
        }

        player.draw(sr);
        coinSpawner.draw(sr);
    }
}
