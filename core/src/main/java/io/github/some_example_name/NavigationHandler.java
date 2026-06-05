package io.github.some_example_name;

import java.util.ArrayList;
import java.util.List;

public class NavigationHandler {
    String currentScreen;
    String previousScreen;
    String tempScreen;
    List<Button> homeButtons;
    List<Button> settingsButtons;
    List<Button> gameButtons;

    public NavigationHandler(){
        homeButtons = new ArrayList<>();
        settingsButtons = new ArrayList<>();
        gameButtons = new ArrayList<>();
        createButtons();
    }

    public void createButtons(){
        homeButtons.add(new Button("gameScreen", 500, 300, 40));
        gameButtons.add(new Button("homeScreen", 600, 400, 50));
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
            case "gameScreen":
               return gameButtons;
        }
        return null;
    }
}
