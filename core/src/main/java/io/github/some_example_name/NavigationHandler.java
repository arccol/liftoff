package io.github.some_example_name;

public class NavigationHandler {
    String currentScreen;
    String previousScreen;
    String tempScreen;
    Button[] buttons;

    public NavigationHandler(){
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

    public Button[] getButtons(String title){
        return buttons;
    }
}
