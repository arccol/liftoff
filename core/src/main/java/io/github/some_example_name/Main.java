package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    FileHandler filehandler;
    List<RealPlanet> planets = new ArrayList<>();
    List<RealPlanet> invenplanets = new ArrayList<>();
    Player player;
    Perlin perlinGen;

    @Override
    public void create() {
        filehandler = new FileHandler("src\\highscores.txt");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

    }

    @Override
    public void dispose() {

    }
}
