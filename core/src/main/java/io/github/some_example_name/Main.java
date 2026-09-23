package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.plaf.ColorUIResource;
import java.util.ArrayList;
import java.util.List;
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
        TUTORIAL
    }
    private Screen currentScreen;
    private boolean mDown;
    private boolean mWasDown;
    private int bufferFrames;
    private boolean ready;
    private Vector2 staticMousePos;
    private int targetFps = 60;
    private float volume = 1.0f;
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
                    windParticles.add(new WindParticle(x+rand.nextInt(1,50), y+rand.nextInt(1,50), 5));
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
        cursor = Gdx.graphics.newCursor(cursorPixmap,10,2);
        cursorPixmap.dispose();
        Gdx.graphics.setCursor(cursor);
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
            case TUTORIAL:
                createTutorialScreen();
                break;
        }
    }

    private void createHomeScreen(){
        bufferFrames = 0;

        TextButton play = new TextButton("Play", skin);
        play.setPosition(810,196);
        play.setSize(300,136);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeScreen(Screen.GAME);
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
            generateDebris();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
            planets.add(new RealPlanet((int) mousePos.x, (int) mousePos.y,20,1, coinList, planetTexture, true, goalFlagTexture));
        }

        if(leftJustPressed && !inventoryDrag){
            staticMousePos.set(mousePos.cpy());
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);

        switch(currentScreen){
            case GAME:
                updatePot();

                player.setDisToPlanet(9999);

                if(player.getLaunched()){
                    player.applyForce(wind);
                }

                for(RealPlanet planet : planets) {
                    if (planet == null) {
                        continue;
                    }
                    planet.draw(batch);

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

                            float strength = (planet.getDens() * planet.getSize() * 15)
                                / (distance * distance);

                            strength = Math.min(strength, 5f);

                            player.applyForce(
                                planetDir.scl(strength)
                            );
                        }
                    }
                }

                if(player.getWin()){
                    player.setWin(false);

                    // advance level

                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
                    spawnPotPlanet();
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
                    wind.set(rand.nextFloat(-0.1f,0.1f),rand.nextFloat(-0.1f,0.1f));
                }

                if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
                    reset();
                }

                // wind code - random chance per frame to change

                for(WindParticle particle : windParticles){
                    particle.updatePos(wind);
                    particle.draw(sr);
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
                    player.applyForce(mouseForce);
                }

                coinSpawner.draw(sr);
                sr.end();
                player.drawTrail(sr);
                player.draw(batch);
                drawPot(sr);
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
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    public void advanceLevel(){

    }

    public void reset(){
        player.reset();
    }
}
