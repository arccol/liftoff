package io.github.some_example_name;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class NavigationHandler {
    String currentScreen;
    String previousScreen;
    String tempScreen;
    List<Button> homeButtons;
    List<Button> settingsButtons;
    List<Button> levelButtons;
    List<Button> customiseButtons;
    List<Button> inGameButtons;

    public NavigationHandler(){
        homeButtons = new ArrayList<>();
        settingsButtons = new ArrayList<>();
        customiseButtons = new ArrayList<>();
        inGameButtons = new ArrayList<>();
        levelButtons = new ArrayList<>();
        createButtons();
    }

    public void createButtons(){

    }

    public void setScreen(String newScreen){
        previousScreen = currentScreen;
        currentScreen = newScreen;
    }

    public String getCurrentScreen(){
        return currentScreen;
    }

    public void goBackScreen(){
        tempScreen = currentScreen;
        currentScreen = previousScreen;
        previousScreen = tempScreen;
    }

    public List<Button> getButtons(String title){
        switch(title){
            case "homeScreen":
                return homeButtons;
            case "levelScreen":
                return levelButtons;
            case "settingsScreen":
                return settingsButtons;
            case "customiseScreen":
                return customiseButtons;
            case "inGameScreen":
                return inGameButtons;
        }
        return null;
    }
}
