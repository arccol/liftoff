package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main extends ApplicationAdapter {
    private List<RealPlanet> planets;
    private List<Debris> debrisList;
    private List<Coin> coinList;
    private CoinSpawner coinSpawner;
    private Player player;
    private ShapeRenderer sr;
    private Vector2 mousePos;
    private Stage stage;
    private Skin skin;
    enum Screen{
        HOME,
        SETTINGS,
        GAME,
        TUTORIAL,
        LEVELSELECT
    }
    private Screen currentScreen;
    private boolean mDown;
    private boolean mWasDown;
    private int bufferFrames;
    private boolean ready;
    private Vector2 staticMousePos;
    private int targetFps = 60;
    private float opacity = 0.5f;
    private Random rand;
    private List<InvenPlanet> potPlanets;
    private InvenPlanet selectedInvenPlanet;
    private Vector2 potCenter;
    private float potRadius;
    private float potTopDegree = 35f;
    private float lossMargin = 40f;
    private int iterations = 100;
    private Perlin debrisNoise;
    private Texture playerTexture;
    private Texture planetTexture;
    private Texture debrisTexture1;
    private Texture debrisTexture2;
    private Texture debrisTexture3;
    private Texture goalFlagTexture;
    private Texture backgroundTexture;
    private Texture titlePageTexture;
    private Texture tutorialTexture;
    private SpriteBatch batch;
    private Vector2 wind;
    private List<WindParticle> windParticles;
    private Pixmap cursorPixmap;
    private Cursor cursor;
    private float windBound = 0.03f;
    private boolean sandbox;
    private Texture whiteTexture;
    private Table sandboxPanel;
    private Label sandboxStatus;
    private int sbRadius = 25;
    private int sbDensity = 2;

    private boolean inventoryDrag;

    @Override
    public void create() {
        planetTexture = new Texture(Gdx.files.internal("planet.png"));
        playerTexture = new Texture(Gdx.files.internal("plane.png"));
        debrisTexture1 = new Texture(Gdx.files.internal("scrap1.png"));
        debrisTexture2 = new Texture(Gdx.files.internal("scrap2.png"));
        debrisTexture3 = new Texture(Gdx.files.internal("scrap3.png"));
        goalFlagTexture = new Texture(Gdx.files.internal("goalFlag.png"));
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        titlePageTexture = new Texture(Gdx.files.internal("liftoff.png"));
        tutorialTexture = new Texture(Gdx.files.internal("tutorial.png"));
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(1f, 1f, 1f, 1f);
        whitePixmap.fill();
        whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();
        cursorPixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        wind = new Vector2();
        wind.set(0,0);
        coinList = new ArrayList<>();
        coinSpawner = new CoinSpawner(coinList);
        rand = new Random();
        sr = new ShapeRenderer();
        windParticles = new ArrayList<>();
        for(int x=0;x<Gdx.graphics.getWidth();x++){
            for(int y=0;y<Gdx.graphics.getHeight();y++) {
                if (x % 100 == 0 && y % 100 == 0 && rand.nextInt(1,5)==4) {
                    windParticles.add(new WindParticle(x+rand.nextInt(1,50), y+rand.nextInt(1,50), 5, windBound));
                }
            }
        }
        planets = new ArrayList<>();
        debrisList = new ArrayList<>();
        player = new Player(600,400,10, playerTexture);
        player.setLaunched(false);
        mousePos = new Vector2();
        staticMousePos = new Vector2();
        potPlanets = new ArrayList<>();
        potCenter = new Vector2(Gdx.graphics.getWidth() - 150, 130);
        potRadius = 100;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        debrisNoise = new Perlin();
        Gdx.input.setInputProcessor(stage);
        changeScreen(Screen.HOME);
        Gdx.graphics.setResizable(false);
        batch = new SpriteBatch();
        sandbox = false;
        cursor = Gdx.graphics.newCursor(cursorPixmap,10,2);
        cursorPixmap.dispose();
        Gdx.graphics.setCursor(cursor);
    }

    public void changeScreen(Screen screen){
        currentScreen = screen;
        if(screen != Screen.GAME){
            selectedInvenPlanet = null;
            sandbox = false;
        }
        sandboxPanel = null;
        sandboxStatus = null;
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
            case TUTORIAL:
                createTutorialScreen();
                break;
            case LEVELSELECT:
                createLevelScreen();
        }
    }

    private void createLevelScreen(){
        bufferFrames = 0;

        TextButton play = new TextButton("Random Level", skin);
        play.setPosition(810,196);
        play.setSize(300,136);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.GAME);
                generateLevel();
            }
        });

        TextButton settings = new TextButton("Sandbox", skin);
        settings.setPosition(300, 196);
        settings.setSize(300,136);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startSandbox();
            }
        });

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
        stage.addActor(play);
        stage.addActor(settings);
    }

    private void createHomeScreen(){
        bufferFrames = 0;

        TextButton play = new TextButton("Play", skin);
        play.setPosition(810,196);
        play.setSize(300,136);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.LEVELSELECT);
            }
        });

        TextButton settings = new TextButton("Settings", skin);
        settings.setPosition(300, 196);
        settings.setSize(300,136);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.SETTINGS);
            }
        });

        TextButton tutorial = new TextButton("Tutorial", skin);
        tutorial.setPosition(1320, 196);
        tutorial.setSize(300,136);
        tutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.TUTORIAL);
            }
        });

        stage.addActor(play);
        stage.addActor(settings);
        stage.addActor(tutorial);
    }

    private void createSettingsScreen(){
        bufferFrames = 0;

        Label title = new Label("Settings", skin);
        title.setPosition(520, 760);
        title.setSize(300, 60);
        stage.addActor(title);

        Label opacityLabel = new Label("Wind Opacity", skin);
        opacityLabel.setPosition(400, 500);
        opacityLabel.setSize(180, 50);

        Slider opacitySlider = new Slider(0f, 1f, 0.01f, false, skin);
        opacitySlider.setValue(opacity);
        opacitySlider.setPosition(600, 515);
        opacitySlider.setSize(220, 30);

        Label opacityValue = new Label((int)(opacity * 100) + "%", skin);
        opacityValue.setPosition(840, 500);
        opacityValue.setSize(100, 50);
        opacitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                opacity = opacitySlider.getValue();
                opacityValue.setText((int)(opacity * 100) + "%");
            }
        });
        stage.addActor(opacityLabel);
        stage.addActor(opacitySlider);
        stage.addActor(opacityValue);

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

    private void createTutorialScreen(){
        bufferFrames = 0;
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

    private void createGameScreen() {
        bufferFrames = 0;
        selectedInvenPlanet = null;

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

        if(sandbox){
            createSandboxPanel();
        }
    }

    private void spawnPotPlanet(){
        int rr = rand.nextInt(20) + 20;
        int rd = rand.nextInt(4) + 1;
        float spawnX = potCenter.x-2+rd;
        float spawnY = potCenter.y + potRadius + 30;
        potPlanets.add(new InvenPlanet((int) spawnX, (int) spawnY, rr, rd, planetTexture));
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
            boolean lost = p.position.dst(potCenter) > potRadius + p.rad + lossMargin;
            if(lost && p == selectedInvenPlanet){
                selectedInvenPlanet = null;
            }
            return lost;
        });
    }

    private void drawPot(ShapeRenderer sr){
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float closedStart = 90f + potTopDegree;
        float closedDegrees = 360f - (potTopDegree * 2f);

        drawRingSegment(sr, potCenter.x, potCenter.y, potRadius, potRadius + 14, closedStart, closedDegrees, new Color(0.35f, 0.22f, 0.12f, 1f));

        for(InvenPlanet p : potPlanets){
            p.draw(sr, p == selectedInvenPlanet, batch);
        }
        sr.end();
    }

    private void drawRingSegment(ShapeRenderer sr, float centerX, float centerY, float innerRadius, float outerRadius, float startAngleDeg, float arcDegrees, Color color) {
        sr.setColor(color);

        int segments = Math.max(6, (int)(12 * (float)Math.cbrt(outerRadius) * (arcDegrees / 360f)));
        float step = arcDegrees / segments;

        for (int i = 0; i < segments; i++) {
            float a0 = (startAngleDeg + i * step) * MathUtils.degreesToRadians;
            float a1 = (startAngleDeg + (i + 1) * step) * MathUtils.degreesToRadians;

            float ix0 = centerX + innerRadius * MathUtils.cos(a0);
            float iy0 = centerY + innerRadius * MathUtils.sin(a0);
            float ox0 = centerX + outerRadius * MathUtils.cos(a0);
            float oy0 = centerY + outerRadius * MathUtils.sin(a0);

            float ix1 = centerX + innerRadius * MathUtils.cos(a1);
            float iy1 = centerY + innerRadius * MathUtils.sin(a1);
            float ox1 = centerX + outerRadius * MathUtils.cos(a1);
            float oy1 = centerY + outerRadius * MathUtils.sin(a1);

            sr.triangle(ix0, iy0, ox0, oy0, ix1, iy1);
            sr.triangle(ox0, oy0, ox1, oy1, ix1, iy1);
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

    public void generateDebris(){
        debrisNoise.createpermtable();

        int screenWidth = Gdx.graphics.getWidth()-300;
        int screenHeight = Gdx.graphics.getHeight();

        int debrisSize = 8;
        int gridStep = 5;
        int octaves = 4;

        float highThreshold = 0.7f;
        float lowThreshold = 0.25f;

        float maxAmplitude = 0f;
        float amp = 1f;
        for(int i = 0; i < octaves; i++){
            maxAmplitude += amp;
            amp *= 0.5f;
        }

        for(int x=gridStep; x<screenWidth-gridStep; x+=gridStep){
            for(int y=gridStep; y<screenHeight-gridStep; y+=gridStep){

                float noiseValue = debrisNoise.FBM(x, y, octaves) / maxAmplitude;

                boolean qualifies = noiseValue > highThreshold || noiseValue < lowThreshold;
                if(!qualifies) continue;

                int jitterX = rand.nextInt(gridStep / 2) - gridStep / 4;
                int jitterY = rand.nextInt(gridStep / 2) - gridStep / 4;
                Vector2 candidate = new Vector2(x + jitterX, y + jitterY);

                if(candidate.x < debrisSize || candidate.x > screenWidth - debrisSize) continue;
                if(candidate.y < debrisSize || candidate.y > screenHeight - debrisSize) continue;


                boolean overlapsPlanet = false;
                for(RealPlanet planet : planets){
                    if(planet == null) continue;
                    if(candidate.dst(planet.getPosition()) < planet.getSize() + debrisSize + 20){
                        overlapsPlanet = true;
                        break;
                    }
                }
                if(overlapsPlanet) continue;

                boolean overlapsDebris = false;
                for(Debris d : debrisList){
                    if(candidate.dst(d.getPosition()) < (debrisSize + d.getSize()) * 1.6f){
                        overlapsDebris = true;
                        break;
                    }
                }
                if(overlapsDebris) continue;

                debrisList.add(new Debris((int) candidate.x, (int) candidate.y, debrisSize, debrisTexture1, debrisTexture2, debrisTexture3));
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(backgroundTexture,0,0);
        batch.end();

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
                    RealPlanet placed = new RealPlanet((int) mousePos.x, (int) mousePos.y, selectedInvenPlanet.rad, selectedInvenPlanet.dens, coinList, planetTexture, false, goalFlagTexture);
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

        if(currentScreen == Screen.GAME && Gdx.input.isKeyJustPressed(Input.Keys.D)){
            generateDebris();
        }

        if(currentScreen == Screen.GAME && Gdx.input.isKeyJustPressed(Input.Keys.F)){
            int goalRad = 20;
            int goalDens = 1;
            if(sandbox){
                goalRad = sbRadius;
                goalDens = sbDensity;
            }
            planets.add(new RealPlanet((int) mousePos.x, (int) mousePos.y, goalRad, goalDens, coinList, planetTexture, true, goalFlagTexture));
        }

        if(leftJustPressed && !inventoryDrag){
            staticMousePos.set(mousePos.cpy());
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);

        switch(currentScreen){
            case GAME:
                updatePot();

                if(sandbox){
                    updateSandbox(delta);
                }

                player.setDisToPlanet(9999);

                if(player.getLaunched()){
                    player.applyForce(wind, delta);
                }

                for(RealPlanet planet : planets) {
                    if (planet == null) {
                        continue;
                    }
                    planet.draw(batch);

                    if(player.getPosition().cpy().sub(planet.getPosition().cpy()).len()<player.getDisToPlanet()){
                        player.setDisToPlanet(player.getPosition().cpy().sub(planet.getPosition().cpy()).len());
                    }

                    player.setWin(false);
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

                            float strength = (planet.getDens() * planet.getSize() * 15)
                                / (distance * distance);

                            strength = Math.min(strength, 5f);

                            player.applyForce(planetDir.scl(strength), delta);
                        }
                    }
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
                    if(!sandbox) {
                        if (coinSpawner.getCoins() > 0) {
                            coinSpawner.changeCoins(-1);
                            spawnPotPlanet();
                        }
                    }else{
                        spawnPotPlanet();
                    }
                }

                for(Debris d : debrisList){
                    if(d.isHit()){
                        for(RealPlanet planet : planets){
                            if(planet == null) continue;

                            if(d.checkColPlanet(planet)){
                                d.delete();
                            }

                            Vector2 planetDir = planet.getPosition().cpy().sub(d.getPosition());

                            float distance = planetDir.len();
                            float gravityRadius = planet.getSize() * 100f;

                            if(distance < gravityRadius && distance > 1) {
                                planetDir.nor();

                                float strength = (planet.getDens() * planet.getSize() * 20) / (distance * distance);

                                strength = Math.min(strength, 5f);
                                strength *= Debris.gravityScale;

                                d.applyForce(planetDir.scl(strength));
                            }
                        }
                    }

                    if(d.checkColPlayer(player)){
                        d.resolveColPlayer(player);
                    }

                    d.updatePos();
                    d.draw(batch);
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
                    wind.set(rand.nextFloat(-windBound,windBound),rand.nextFloat(-windBound,windBound));
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
                    reset();
                }

                for(WindParticle particle : windParticles){
                    particle.updatePos(wind);
                    particle.draw(sr, opacity);
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
                    player.genTraj(planets,sr,mouseForce,wind);
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
                    player.applyForce(mouseForce, delta);
                }

                coinSpawner.draw(sr);
                sr.end();
                player.drawTrail(sr);
                player.draw(batch);
                drawPot(sr);
                checkWin();
                break;
            case TUTORIAL:
                batch.begin();
                batch.setColor(Color.WHITE);
                batch.draw(tutorialTexture,0,0);
                batch.end();
                break;
            case HOME:
                batch.begin();
                batch.setColor(Color.WHITE);
                batch.draw(titlePageTexture,0,0);
                batch.end();
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
        whiteTexture.dispose();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    private void startSandbox(){
        sandbox = true;
        changeScreen(Screen.GAME);
        clearWorld();
        player.reset();
        wind.set(0, 0);
        sbRadius = 25;
        sbDensity = 2;
    }

    private void checkWin(){
        if(player.getWin()&&coinSpawner.getCoins()>4){
            generateLevel();
        }
    }

    private void clearWorld(){
        planets.clear();
        debrisList.clear();
        coinList.clear();
        coinSpawner.changeCoins(-coinSpawner.getCoins());
    }

    private void createSandboxPanel(){
        String[][] binds = {
            {"R", "reset player"},
            {"C", "clear everything"},
            {"SPACE", "place normal planet"},
            {"F", "place goal planet"},
            {"Z", "delete"},
            {"[ ]", "change radius"},
            {"- =", "change density"},
            {"E", "spawn single debris"},
            {"D", "spawn debris field"},
            {"X", "clear debris"},
            {"W / Q", "random / no wind"},
            {"Arrows", "adjust wind"},
            {"P", "add planet to inventory"},
        };

        Table t = new Table();
        t.setBackground(new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0f, 0f, 0f, 0.6f)));
        t.pad(10);

        Label title = new Label("Sandbox                                (H: hide)", skin);
        title.setColor(0.5f, 0.8f, 1f, 1f);
        t.add(title).colspan(2).left().padBottom(6).row();

        for(String[] b : binds){
            Label key = new Label(b[0], skin);
            key.setColor(1f, 0.85f, 0.3f, 1f);
            t.add(key).left().padRight(14);
            t.add(new Label(b[1], skin)).left().row();
        }

        sandboxStatus = new Label("", skin);
        sandboxStatus.setColor(0.7f, 1f, 0.7f, 1f);
        updateSandboxStatus();
        t.add(sandboxStatus).colspan(2).left().padTop(8);
        t.pack();
        t.setPosition(Gdx.graphics.getWidth()-t.getWidth()-10, Gdx.graphics.getHeight()-t.getHeight()-10);
        stage.addActor(t);
        sandboxPanel = t;
    }

    private void updateSandboxStatus(){
        if(sandboxStatus == null) return;
        sandboxStatus.setText(String.format(Locale.UK, "Wind  %+.3f, %+.3f\nPlanet  radius %d  density %d\nPlanets %d   Debris %d",
                                                                wind.x, wind.y,             sbRadius, sbDensity, planets.size(), debrisList.size()));
    }

    private void updateSandbox(float delta){
        if(Gdx.input.isKeyJustPressed(Input.Keys.H) && sandboxPanel != null){
            sandboxPanel.setVisible(!sandboxPanel.isVisible());
        }

        float windMax = windBound * 2f;
        float windStep = 0.05f * delta;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            wind.x -= windStep;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            wind.x += windStep;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            wind.y -= windStep;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            wind.y += windStep;
        }
        wind.x = MathUtils.clamp(wind.x, -windMax, windMax);
        wind.y = MathUtils.clamp(wind.y, -windMax, windMax);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
            wind.set(0, 0);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)) {
            sbRadius = Math.max(5, sbRadius - 5);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)){
            sbRadius = Math.min(80, sbRadius + 5);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS)){
            sbDensity = Math.max(1, sbDensity - 1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)){
            sbDensity = Math.min(9, sbDensity + 1);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isOverPot(mousePos)){
            RealPlanet placed = new RealPlanet((int) mousePos.x, (int) mousePos.y, sbRadius, sbDensity,
                coinList, planetTexture, false, goalFlagTexture);
            placed.generateCoins();
            planets.add(placed);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            boolean removed = false;
            for(int i = planets.size() - 1; i >= 0; i--){
                RealPlanet p = planets.get(i);
                if(p != null && p.getPosition().dst(mousePos) <= p.getSize() + 6){
                    planets.remove(i);
                    coinList.removeIf(c -> c.getPos().dst(p.getPosition()) <= p.getSize() + p.offset + 3);
                    removed = true;
                    break;
                }
            }
            if(!removed){
                Debris nearest = null;
                float best = 30f;
                for(Debris d : debrisList){
                    float dist = d.getPosition().dst(mousePos);
                    if(dist < best){
                        best = dist;
                        nearest = d;
                    }
                }
                if(nearest != null){
                    debrisList.remove(nearest);
                }
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
            debrisList.add(new Debris((int) mousePos.x, (int) mousePos.y, 8, debrisTexture1, debrisTexture2, debrisTexture3));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.X)){
            debrisList.clear();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            clearWorld();
        }

        updateSandboxStatus();
    }

    public void generateLevel(){
        reset();
        clearWorld();
        for(int i=0;i<rand.nextInt(1,10);i++){
            int rx = rand.nextInt(100,1820);
            int ry = rand.nextInt(100,980);
            int rr = rand.nextInt(15,40);
            int rd = rand.nextInt(1,5);
            planets.add(new RealPlanet(rx,ry,rr,rd,coinList,planetTexture,false,goalFlagTexture));
            planets.get(i).generateCoins();
        }
        int rx = rand.nextInt(100,1820);
        int ry = rand.nextInt(100,980);
        int rr = rand.nextInt(10,20);
        int rd = rand.nextInt(1,3);
        planets.add(new RealPlanet(rx,ry,rr,rd,coinList,planetTexture,true,goalFlagTexture));
        wind.set(rand.nextFloat(-windBound,windBound),rand.nextFloat(-windBound,windBound));
        for(int i=0;i<3;i++) {
            generateDebris();
        }
    }

    public void reset(){
        player.reset();
    }
}
