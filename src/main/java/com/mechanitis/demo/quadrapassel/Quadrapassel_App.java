package com.mechanitis.demo.quadrapassel;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Quadrapassel_App extends Application {

    @Override
    public void start(Stage stage) {
        GameplayScene gameplayScene = new GameplayScene(new AnchorPane(), 500, 700);
        stage.setScene(gameplayScene);
        stage.setResizable(false);
        stage.show();
        gameplayScene.Run();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                gameplayScene.Die();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }


}
