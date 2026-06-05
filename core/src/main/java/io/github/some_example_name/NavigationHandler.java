package io.github.some_example_name;

import java.util.List;

public class NavigationHandler {
    String currentScreen;
    String previousScreen;
    String tempScreen;
    List<Button> homeButtons;
    List<Button> settingsButtons;
    List<Button> gameButtons;

    public NavigationHandler(){
    }

    public void createButtons(){
        homeButtons.add(new Button("play", 100, 100, 20));
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
            //case "gameScreen":
            //    return gameButtons;
        }
        return null;
    }
}
