package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends ApplicationAdapter {
    List<RealPlanet> planets;
    List<Debris> debrisList;
    List<Coin> coinList;
    CoinSpawner coinSpawner;
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
    private int targetFps = 60;
    private float volume = 1.0f;
    Random rand;
    List<InvenPlanet> potPlanets;
    InvenPlanet selectedInvenPlanet;
    Vector2 potCenter;
    float potRadius;
    private static final float potTopDegree = 35f;
    private static final float lossMargin = 40f;
    private static final int iterations = 100;

    private boolean inventoryDrag;

    @Override
    public void create() {
        coinList = new ArrayList<>();
        coinSpawner = new CoinSpawner(coinList);
        rand = new Random();
        sr = new ShapeRenderer();
        planets = new ArrayList<>();
        debrisList = new ArrayList<>();
        player = new Player(600,400,10);
        player.setLaunched(false);
        mousePos = new Vector2();
        staticMousePos = new Vector2();
        potPlanets = new ArrayList<>();
        potCenter = new Vector2(Gdx.graphics.getWidth() - 150, 130);
        potRadius = 100;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        changeScreen(Screen.HOME);
        Gdx.graphics.setResizable(false);
    }

    public void changeScreen(Screen screen){
        currentScreen = screen;
        if(screen != Screen.GAME){
            selectedInvenPlanet = null;
        }
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

        Label title = new Label("Settings", skin);
        title.setPosition(520, 760);
        title.setSize(300, 60);
        stage.addActor(title);

        Label volumeLabel = new Label("Volume", skin);
        volumeLabel.setPosition(400, 500);
        volumeLabel.setSize(180, 50);

        Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(volume);
        volumeSlider.setPosition(600, 515);
        volumeSlider.setSize(220, 30);

        Label volumeValue = new Label((int)(volume * 100) + "%", skin);
        volumeValue.setPosition(840, 500);
        volumeValue.setSize(100, 50);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                volume = volumeSlider.getValue();
                volumeValue.setText((int)(volume * 100) + "%");
            }
        });
        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
        stage.addActor(volumeValue);

        Label fpsLabel = new Label("Framerate", skin);
        fpsLabel.setPosition(400, 380);
        fpsLabel.setSize(180, 50);

        SelectBox<String> fpsBox = new SelectBox<>(skin);
        fpsBox.setItems("30 FPS", "60 FPS", "120 FPS", "144 FPS");
        fpsBox.setSelected(targetFps + " FPS");
        fpsBox.setPosition(600, 380);
        fpsBox.setSize(220, 50);
        fpsBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetFps = Integer.parseInt(fpsBox.getSelected().replace(" FPS", ""));
                Gdx.graphics.setForegroundFPS(targetFps);
            }
        });
        stage.addActor(fpsLabel);
        stage.addActor(fpsBox);

        TextButton back = new TextButton("Back", skin);
        back.setPosition(0, Gdx.graphics.getHeight() - 60);
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
        selectedInvenPlanet = null;

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


    private void spawnPotPlanet(){
        int rr = rand.nextInt(20) + 20;
        int rd = rand.nextInt(4) + 1;
        float spawnX = potCenter.x-2+rd;
        float spawnY = potCenter.y + potRadius + 30;
        potPlanets.add(new InvenPlanet((int) spawnX, (int) spawnY, rr, rd));
    }

    private void updatePot(){
        for(InvenPlanet p : potPlanets){
            p.applyGrav();
            p.updatePos();
            p.constrainToPot(potCenter, potRadius, potTopDegree);
        }

        for(int iter = 0; iter < iterations; iter++){
            for(int i = 0; i < potPlanets.size(); i++){
                for(int j = i + 1; j < potPlanets.size(); j++){
                    InvenPlanet a = potPlanets.get(i);
                    InvenPlanet b = potPlanets.get(j);
                    if(a.checkColPlanet(b)){
                        a.hitCirc(b);
                    }
                }
            }
            for(InvenPlanet p : potPlanets){
                p.constrainToPot(potCenter, potRadius, potTopDegree);
            }
        }

        potPlanets.removeIf(p -> {
            boolean lost = p.position.dst(potCenter) > potRadius + p.r + lossMargin;
            if(lost && p == selectedInvenPlanet){
                selectedInvenPlanet = null;
            }
            return lost;
        });
    }

    private void drawPot(ShapeRenderer sr){
        float closedStart = 90f + potTopDegree;
        float closedDegrees = 360f - (potTopDegree * 2f);

        sr.setColor(new Color(0.35f, 0.22f, 0.12f, 1f));
        sr.arc(potCenter.x, potCenter.y, potRadius + 14, closedStart, closedDegrees);
        sr.setColor(new Color(0.15f, 0.15f, 0.2f, 1f));
        sr.arc(potCenter.x, potCenter.y, potRadius, closedStart, closedDegrees);

        for(InvenPlanet p : potPlanets){
            p.draw(sr, p == selectedInvenPlanet);
        }
    }

    private InvenPlanet getPotPlanetAt(Vector2 pos){
        for(int i = potPlanets.size() - 1; i >= 0; i--){
            InvenPlanet p = potPlanets.get(i);
            if(p.overlap(pos.x, pos.y)){
                return p;
            }
        }
        return null;
    }

    private boolean isOverPot(Vector2 pos){
        return potCenter.dst(pos) <= potRadius + 14;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        mousePos.set(
            Gdx.input.getX(),
            Gdx.graphics.getHeight() - Gdx.input.getY()
        );

        boolean leftDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean leftJustPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        if(leftJustPressed){
            inventoryDrag = false;
            if(currentScreen == Screen.GAME){
                InvenPlanet clickedMarble = getPotPlanetAt(mousePos);
                if(clickedMarble != null){
                    selectedInvenPlanet = (selectedInvenPlanet == clickedMarble) ? null : clickedMarble;
                    inventoryDrag = true;
                } else if(isOverPot(mousePos)){

                    inventoryDrag = true;
                } else if(selectedInvenPlanet != null){
                    RealPlanet placed = new RealPlanet(
                        (int) mousePos.x, (int) mousePos.y,
                        selectedInvenPlanet.r, selectedInvenPlanet.dens, coinList
                    );
                    placed.generateCoins();
                    planets.add(placed);

                    potPlanets.remove(selectedInvenPlanet);
                    selectedInvenPlanet = null;

                    inventoryDrag = true;
                }
            }
        }
        if(!leftDown){
            inventoryDrag = false;
        }

        if(ready) {
            mDown = leftDown && !inventoryDrag;
        }
        if(!ready) {
            ready = leftJustPressed && !inventoryDrag;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)){
            ready = false;
            selectedInvenPlanet = null;
        }

        if(leftJustPressed && !inventoryDrag){
            staticMousePos.set(mousePos.cpy());
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);

        switch(currentScreen){
            case GAME:
                updatePot();

                player.setDisToPlanet(9999);

                for(RealPlanet planet : planets) {
                    if (planet == null) {
                        continue;
                    }
                    planet.draw(sr);

                    if(player.getPosition().cpy().sub(planet.getPosition().cpy()).len()<player.getDisToPlanet()){
                        player.setDisToPlanet(player.getPosition().cpy().sub(planet.getPosition().cpy()).len());
                    }

                    if(player.checkColPlanet(planet)) {
                        player.moveCol(planet);
                    }

                    if (player.getLaunched()) {

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

                if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
                    spawnPotPlanet();
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
                    debrisList.add(new Debris((int) mousePos.x, (int) mousePos.y, 8));
                }

                for(Debris d : debrisList){
                    if(d.isHit()){
                        for(RealPlanet planet : planets){
                            if(planet == null) continue;

                            if(d.checkColPlanet(planet)){
                                d.moveCol(planet);
                            }

                            Vector2 planetDir = planet.getPosition().cpy()
                                .sub(d.getPosition());

                            float distance = planetDir.len();
                            float gravityRadius = planet.getSize() * 100f;

                            if(distance < gravityRadius && distance > 1) {
                                planetDir.nor();

                                float strength = (planet.getDens() * planet.getSize() * 20)
                                    / (distance * distance);

                                strength = Math.min(strength, 5f);
                                strength *= Debris.GRAVITY_SCALE;

                                d.applyForce(planetDir.scl(strength));
                            }
                        }
                    }

                    if(d.checkColPlayer(player)){
                        d.resolveColPlayer(player);
                    }

                    d.updatePos();
                    d.draw(sr);
                }

                player.updatePos();

                if(player.getLaunched() && player.getLaunchAvailable()){
                    player.setLaunchAvailable(true);
                }

                coinSpawner.pickupCoins(player.getPosition());

                Vector2 mouseForce = mousePos.cpy()
                    .sub(staticMousePos)
                    .scl(0.03f)
                    .scl(-1);

                mouseForce.scl(mouseForce.len());
                mouseForce.limit(10f);

                if(mDown&&(!player.getLaunched()||player.getLaunchAvailable())&&ready){
                    player.genTrail(planets,sr,mouseForce);
                    Vector2 ghostMouse = mousePos.cpy()
                        .sub(staticMousePos)
                        .limit(100)
                        .add(staticMousePos);
                    int length = (int) ghostMouse.cpy().sub(staticMousePos).len();
                    sr.setColor(new Color((float)length/100,0.5f-(float)length/300,0.1f, 1));
                    sr.rectLine(ghostMouse,staticMousePos,6);
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

                if((!player.getLaunched()||player.getLaunchAvailable())&&!mDown&&mWasDown&&bufferFrames>10){
                    player.setLaunched(true);
                    player.setLaunchAvailable(false);
                    player.applyForce(mouseForce);
                }

                player.draw(sr);
                coinSpawner.draw(sr);

                drawPot(sr);
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
